package com.example.mobilecomputingproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilecomputingproject.domain.model.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val isFavorite: Boolean = false
)

fun PostEntity.toDomain(): Post = Post(id, userId, title, body, isFavorite)
fun Post.toEntity(): PostEntity = PostEntity(id, userId, title, body, isFavorite)
