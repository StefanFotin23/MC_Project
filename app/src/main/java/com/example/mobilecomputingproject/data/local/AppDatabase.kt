package com.example.mobilecomputingproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

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