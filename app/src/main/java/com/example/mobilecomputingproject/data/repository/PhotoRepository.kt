package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.toDomain
import com.example.mobilecomputingproject.data.local.toEntity
import com.example.mobilecomputingproject.data.remote.RetrofitInstance
import com.example.mobilecomputingproject.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PhotoRepository(
    private val db: AppDatabase
) {

    private val photoDao = db.photoDao()

    /**
     * Returns a flow of photos from the local cache.
     */
    fun getPhotos(): Flow<List<Photo>> = photoDao.getAllPhotos().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches photos from the remote API and saves them to the local cache.
     */
    suspend fun refreshPhotos() {
        withContext(Dispatchers.IO) {
            try {
                val remotePhotos = RetrofitInstance.api.getPhotos()
                Log.d("Repository", "Fetched ${remotePhotos.size} photos from API")
                photoDao.insertAll(remotePhotos.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching photos: ${e.message}")
            }
        }
    }

    /**
     * Get a specific photo by its ID from the local cache.
     */
    fun getPhotoById(id: Int): Flow<Photo?> = photoDao.getPhotoById(id).map { it?.toDomain() }

    /**
     * Fetches a single photo from the remote API and saves it to the local cache.
     */
    suspend fun refreshPhoto(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val remotePhoto = RetrofitInstance.api.getPhotoById(id)
                photoDao.insertAll(listOf(remotePhoto.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching photo $id: ${e.message}")
            }
        }
    }
}
