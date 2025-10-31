package com.example.mobilecomputingproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilecomputingproject.domain.model.Photo

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)

fun PhotoEntity.toDomain(): Photo = Photo(albumId, id, title, url, thumbnailUrl)
fun Photo.toEntity(): PhotoEntity = PhotoEntity(id, albumId, title, url, thumbnailUrl)
