package com.example.mobilecomputingproject

import android.app.Application
import androidx.room.Room
import com.example.mobilecomputingproject.data.local.AppDatabase

class MobileComputingProjectApplication : Application() {
    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "posts_db"
        ).build()
    }
}
