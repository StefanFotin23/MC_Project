package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.entity.toDomain
import com.example.mobilecomputingproject.data.local.entity.toEntity
import com.example.mobilecomputingproject.data.api.RetrofitInstance
import com.example.mobilecomputingproject.domain.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CommentRepository(
    private val db: AppDatabase
) {

    private val commentDao = db.commentDao()

    /**
     * Returns a flow of comments from the local cache.
     */
    fun getComments(): Flow<List<Comment>> = commentDao.getAllComments().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches comments from the API and saves them to the local cache.
     */
    suspend fun refreshComments() {
        withContext(Dispatchers.IO) {
            try {
                val apiComments = RetrofitInstance.api.getComments()
                Log.d("Repository", "Fetched ${apiComments.size} comments from API")
                commentDao.insertAll(apiComments.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching comments: ${e.message}")
            }
        }
    }

    /**
     * Get a specific comment by its ID from the local cache.
     */
    fun getCommentById(id: Int): Flow<Comment?> = commentDao.getCommentById(id).map { it?.toDomain() }

    /**
     * Fetches a single comment from the API and saves it to the local cache.
     */
    suspend fun refreshComment(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val apiComment = RetrofitInstance.api.getCommentById(id)
                commentDao.insertAll(listOf(apiComment.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching comment $id: ${e.message}")
            }
        }
    }
}
