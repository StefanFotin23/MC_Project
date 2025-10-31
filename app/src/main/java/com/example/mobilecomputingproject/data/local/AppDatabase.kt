package com.example.mobilecomputingproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mobilecomputingproject.data.local.dao.AlbumDao
import com.example.mobilecomputingproject.data.local.dao.CommentDao
import com.example.mobilecomputingproject.data.local.dao.PhotoDao
import com.example.mobilecomputingproject.data.local.dao.PostDao
import com.example.mobilecomputingproject.data.local.dao.TodoDao
import com.example.mobilecomputingproject.data.local.dao.UserDao
import com.example.mobilecomputingproject.data.local.entity.AlbumEntity
import com.example.mobilecomputingproject.data.local.entity.CommentEntity
import com.example.mobilecomputingproject.data.local.entity.PhotoEntity
import com.example.mobilecomputingproject.data.local.entity.PostEntity
import com.example.mobilecomputingproject.data.local.entity.TodoEntity
import com.example.mobilecomputingproject.data.local.entity.UserEntity

@Database(
    entities = [
        PostEntity::class,
        CommentEntity::class,
        AlbumEntity::class,
        PhotoEntity::class,
        TodoEntity::class,
        UserEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun albumDao(): AlbumDao
    abstract fun photoDao(): PhotoDao
    abstract fun todoDao(): TodoDao
    abstract fun userDao(): UserDao
}