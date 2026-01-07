package com.example.mobilecomputingproject.dependency_injection

import com.example.mobilecomputingproject.data.api.JsonPlaceholderApi
import com.example.mobilecomputingproject.data.local.dao.AlbumDao
import com.example.mobilecomputingproject.data.local.dao.CommentDao
import com.example.mobilecomputingproject.data.local.dao.PhotoDao
import com.example.mobilecomputingproject.data.local.dao.PostDao
import com.example.mobilecomputingproject.data.local.dao.TodoDao
import com.example.mobilecomputingproject.data.local.dao.UserDao
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
    fun providePostRepository(postDao: PostDao, api: JsonPlaceholderApi): PostRepository {
        return PostRepository(postDao, api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, api: JsonPlaceholderApi): UserRepository {
        return UserRepository(userDao, api)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(commentDao: CommentDao, api: JsonPlaceholderApi): CommentRepository {
        return CommentRepository(commentDao, api)
    }

    @Provides
    @Singleton
    fun provideAlbumRepository(albumDao: AlbumDao, api: JsonPlaceholderApi): AlbumRepository {
        return AlbumRepository(albumDao, api)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(photoDao: PhotoDao, api: JsonPlaceholderApi): PhotoRepository {
        return PhotoRepository(photoDao, api)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao, api: JsonPlaceholderApi): TodoRepository {
        return TodoRepository(todoDao, api)
    }
}
