package com.example.mobilecomputingproject.data.remote

import com.example.mobilecomputingproject.domain.model.Post
import retrofit2.http.GET
import retrofit2.http.Path


interface PostApi {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Post
}