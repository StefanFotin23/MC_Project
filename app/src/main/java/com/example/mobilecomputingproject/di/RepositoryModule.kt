package com.example.mobilecomputingproject.di

import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.AlbumRepository
import com.example.mobilecomputingproject.data.repository.CommentRepository
import com.example.mobilecomputingproject.data.repository.PhotoRepository
import com.example.mobilecomputingproject.data.repository.PostRepository
import com.example.mobilecomputingproject.data.repository.TodoRepository
import com.example.mobilecomputingproject.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(db: AppDatabase): PostRepository {
        return PostRepository(db)
    }

    @Provides
    @Singleton
    fun provideUserRepository(db: AppDatabase): UserRepository {
        return UserRepository(db)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(db: AppDatabase): CommentRepository {
        return CommentRepository(db)
    }

    @Provides
    @Singleton
    fun provideAlbumRepository(db: AppDatabase): AlbumRepository {
        return AlbumRepository(db)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(db: AppDatabase): PhotoRepository {
        return PhotoRepository(db)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: AppDatabase): TodoRepository {
        return TodoRepository(db)
    }
}
