package com.example.mobilecomputingproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilecomputingproject.domain.model.Comment

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)

fun CommentEntity.toDomain(): Comment = Comment(postId, id, name, email, body)
fun Comment.toEntity(): CommentEntity = CommentEntity(id, postId, name, email, body)
