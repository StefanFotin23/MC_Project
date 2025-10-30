package com.example.mobilecomputingproject.data.remote

import com.example.mobilecomputingproject.domain.model.Album
import com.example.mobilecomputingproject.domain.model.Comment
import com.example.mobilecomputingproject.domain.model.Photo
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.domain.model.Todo
import com.example.mobilecomputingproject.domain.model.User
import retrofit2.http.GET
import retrofit2.http.Path


interface JsonPlaceholderApi {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Post

    @GET("comments")
    suspend fun getComments(): List<Comment>

    @GET("comments/{id}")
    suspend fun getCommentById(@Path("id") id: Int): Comment

    @GET("albums")
    suspend fun getAlbums(): List<Album>

    @GET("albums/{id}")
    suspend fun getAlbumById(@Path("id") id: Int): Album

    @GET("photos")
    suspend fun getPhotos(): List<Photo>

    @GET("photos/{id}")
    suspend fun getPhotoById(@Path("id") id: Int): Photo

    @GET("todos")
    suspend fun getTodos(): List<Todo>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int): Todo

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): User
}