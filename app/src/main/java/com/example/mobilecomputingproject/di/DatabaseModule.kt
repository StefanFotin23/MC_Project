package com.example.mobilecomputingproject.di

import android.content.Context
import androidx.room.Room
import com.example.mobilecomputingproject.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "posts_db"
        ).build()
    }

    @Provides
    fun providePostDao(appDatabase: AppDatabase) = appDatabase.postDao()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun provideCommentDao(appDatabase: AppDatabase) = appDatabase.commentDao()

    @Provides
    fun provideAlbumDao(appDatabase: AppDatabase) = appDatabase.albumDao()

    @Provides
    fun providePhotoDao(appDatabase: AppDatabase) = appDatabase.photoDao()

    @Provides
    fun provideTodoDao(appDatabase: AppDatabase) = appDatabase.todoDao()
}
