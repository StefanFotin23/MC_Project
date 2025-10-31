package com.example.mobilecomputingproject.di

import com.example.mobilecomputingproject.data.api.JsonPlaceholderApi
import com.example.mobilecomputingproject.data.api.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJsonPlaceholderApi(): JsonPlaceholderApi {
        return RetrofitInstance.api
    }
}
