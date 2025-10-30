package com.example.mobilecomputingproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilecomputingproject.domain.model.Album

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String
)

fun AlbumEntity.toDomain(): Album = Album(userId, id, title)
fun Album.toEntity(): AlbumEntity = AlbumEntity(id, userId, title)
