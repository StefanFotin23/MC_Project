package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.toDomain
import com.example.mobilecomputingproject.data.local.toEntity
import com.example.mobilecomputingproject.data.remote.RetrofitInstance
import com.example.mobilecomputingproject.domain.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AlbumRepository(
    private val db: AppDatabase
) {

    private val albumDao = db.albumDao()

    /**
     * Returns a flow of albums from the local cache.
     */
    fun getAlbums(): Flow<List<Album>> = albumDao.getAllAlbums().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches albums from the remote API and saves them to the local cache.
     */
    suspend fun refreshAlbums() {
        withContext(Dispatchers.IO) {
            try {
                val remoteAlbums = RetrofitInstance.api.getAlbums()
                Log.d("Repository", "Fetched ${remoteAlbums.size} albums from API")
                albumDao.insertAll(remoteAlbums.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching albums: ${e.message}")
            }
        }
    }

    /**
     * Get a specific album by its ID from the local cache.
     */
    fun getAlbumById(id: Int): Flow<Album?> = albumDao.getAlbumById(id).map { it?.toDomain() }

    /**
     * Fetches a single album from the remote API and saves it to the local cache.
     */
    suspend fun refreshAlbum(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val remoteAlbum = RetrofitInstance.api.getAlbumById(id)
                albumDao.insertAll(listOf(remoteAlbum.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching album $id: ${e.message}")
            }
        }
    }
}
