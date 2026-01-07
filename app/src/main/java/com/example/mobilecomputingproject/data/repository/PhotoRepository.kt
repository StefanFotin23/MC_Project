package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.api.JsonPlaceholderApi
import com.example.mobilecomputingproject.data.local.dao.PhotoDao
import com.example.mobilecomputingproject.data.local.entity.toDomain
import com.example.mobilecomputingproject.data.local.entity.toEntity
import com.example.mobilecomputingproject.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PhotoRepository(
    private val photoDao: PhotoDao,
    private val api: JsonPlaceholderApi
) {

    /**
     * Returns a flow of photos from the local cache.
     */
    fun getPhotos(): Flow<List<Photo>> = photoDao.getAllPhotos().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches photos from the API and saves them to the local cache.
     */
    suspend fun refreshPhotos() {
        withContext(Dispatchers.IO) {
            try {
                val apiPhotos = api.getPhotos()
                val localPhotos = photoDao.getAllPhotos().first()
                val favoritePhotoIds = localPhotos.filter { it.isFavorite }.map { it.id }.toSet()

                val updatedPhotos = apiPhotos.map { apiPhoto ->
                    val entity = apiPhoto.toEntity()
                    if (entity.id in favoritePhotoIds) {
                        entity.copy(isFavorite = true)
                    } else {
                        entity
                    }
                }
                photoDao.insertAll(updatedPhotos)
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
     * Fetches a single photo from the API and saves it to the local cache.
     */
    suspend fun refreshPhoto(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val apiPhoto = api.getPhotoById(id)
                photoDao.insertAll(listOf(apiPhoto.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching photo $id: ${e.message}")
            }
        }
    }

    suspend fun updateFavoriteStatus(photoId: Int, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            photoDao.updateFavoriteStatus(photoId, isFavorite)
        }
    }
}
