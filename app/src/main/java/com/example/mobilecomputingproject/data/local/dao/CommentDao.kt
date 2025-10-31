package com.example.mobilecomputingproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobilecomputingproject.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CommentDao {
    @Query("SELECT * FROM comments")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :id")
    fun getCommentById(id: Int): Flow<CommentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    @Query("DELETE FROM comments")
    suspend fun clearAll()

    suspend fun clearAndInsert(comments: List<CommentEntity>) {
        clearAll()
        insertAll(comments)
    }
}
