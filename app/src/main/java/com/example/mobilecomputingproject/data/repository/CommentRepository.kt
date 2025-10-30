package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.toDomain
import com.example.mobilecomputingproject.data.local.toEntity
import com.example.mobilecomputingproject.data.remote.RetrofitInstance
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
     * Fetches comments from the remote API and saves them to the local cache.
     */
    suspend fun refreshComments() {
        withContext(Dispatchers.IO) {
            try {
                val remoteComments = RetrofitInstance.api.getComments()
                Log.d("Repository", "Fetched ${remoteComments.size} comments from API")
                commentDao.insertAll(remoteComments.map { it.toEntity() })
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
     * Fetches a single comment from the remote API and saves it to the local cache.
     */
    suspend fun refreshComment(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val remoteComment = RetrofitInstance.api.getCommentById(id)
                commentDao.insertAll(listOf(remoteComment.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching comment $id: ${e.message}")
            }
        }
    }
}
