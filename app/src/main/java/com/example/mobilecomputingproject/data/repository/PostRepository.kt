package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.entity.toDomain
import com.example.mobilecomputingproject.data.local.entity.toEntity
import com.example.mobilecomputingproject.data.api.RetrofitInstance
import com.example.mobilecomputingproject.domain.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PostRepository(
    private val db: AppDatabase
) {

    private val postDao = db.postDao()

    /**
     * Returns a flow of posts from the local cache.
     */
    fun getPosts(): Flow<List<Post>> = postDao.getAllPosts().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches posts from the API and saves them to the local cache.
     */
    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            try {
                val apiPosts = RetrofitInstance.api.getPosts()
                Log.d("Repository", "Fetched ${apiPosts.size} posts from API")

                // Get current favorite statuses from local DB
                val existingPosts = postDao.getAllPosts().first()
                val favoriteStatuses = existingPosts.associate { it.id to it.isFavorite }

                val postsToInsert = apiPosts.map { apiPost ->
                    apiPost.toEntity().copy(isFavorite = favoriteStatuses[apiPost.id] ?: false)
                }
                postDao.insertAll(postsToInsert)
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching posts: ${e.message}")
            }
        }
    }

    /**
     * Get a specific post by its ID from the local cache.
     */
    fun getPostById(id: Int): Flow<Post?> = postDao.getPostById(id).map { it?.toDomain() }

    /**
     * Returns a flow of posts from the local cache for a specific user.
     */
    fun getPostsByUser(userId: Int): Flow<List<Post>> = postDao.getPostsByUser(userId).map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches a single post from the API and saves it to the local cache.
     */
    suspend fun refreshPost(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val apiPost = RetrofitInstance.api.getPostById(id)
                postDao.insertAll(listOf(apiPost.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching post $id: ${e.message}")
            }
        }
    }

    /**
     * Fetches posts for a specific user from the API and saves them to the local cache.
     */
    suspend fun updateFavoriteStatus(postId: Int, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            postDao.updateFavoriteStatus(postId, isFavorite)
        }
    }

    /**
     * Fetches posts for a specific user from the API and saves them to the local cache.
     */
    suspend fun refreshPostsByUser(userId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val apiPosts = RetrofitInstance.api.getPostsByUser(userId)
                Log.d("Repository", "Fetched ${apiPosts.size} posts from API for user $userId")

                // Get current favorite statuses from local DB
                val existingPosts = postDao.getAllPosts().first()
                val favoriteStatuses = existingPosts.associate { it.id to it.isFavorite }

                val postsToInsert = apiPosts.map { apiPost ->
                    apiPost.toEntity().copy(isFavorite = favoriteStatuses[apiPost.id] ?: false)
                }
                postDao.insertAll(postsToInsert)
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching posts for user $userId: ${e.message}")
            }
        }
    }
}