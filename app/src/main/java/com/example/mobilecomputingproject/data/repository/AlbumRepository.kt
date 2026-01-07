package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.api.JsonPlaceholderApi
import com.example.mobilecomputingproject.data.local.dao.AlbumDao
import com.example.mobilecomputingproject.data.local.entity.toDomain
import com.example.mobilecomputingproject.data.local.entity.toEntity
import com.example.mobilecomputingproject.domain.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AlbumRepository(
    private val albumDao: AlbumDao,
    private val api: JsonPlaceholderApi
) {

    /**
     * Returns a flow of albums from the local cache.
     */
    fun getAlbums(): Flow<List<Album>> = albumDao.getAllAlbums().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches albums from the API and saves them to the local cache.
     */
    suspend fun refreshAlbums() {
        withContext(Dispatchers.IO) {
            try {
                val apiAlbums = api.getAlbums()
                Log.d("Repository", "Fetched ${apiAlbums.size} albums from API")
                albumDao.insertAll(apiAlbums.map { it.toEntity() })
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
     * Fetches a single album from the API and saves it to the local cache.
     */
    suspend fun refreshAlbum(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val apiAlbum = api.getAlbumById(id)
                albumDao.insertAll(listOf(apiAlbum.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching album $id: ${e.message}")
            }
        }
    }
}
