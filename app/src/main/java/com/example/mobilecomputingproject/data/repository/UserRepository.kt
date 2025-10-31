package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.entity.toDomain
import com.example.mobilecomputingproject.data.local.entity.toEntity
import com.example.mobilecomputingproject.data.api.RetrofitInstance
import com.example.mobilecomputingproject.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserRepository(
    private val db: AppDatabase
) {

    private val userDao = db.userDao()

    /**
     * Returns a flow of users from the local cache.
     */
    fun getUsers(): Flow<List<User>> = userDao.getAllUsers().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches users from the API and saves them to the local cache.
     */
    suspend fun refreshUsers() {
        withContext(Dispatchers.IO) {
            try {
                val apiUsers = RetrofitInstance.api.getUsers()
                Log.d("Repository", "Fetched ${apiUsers.size} users from API")
                userDao.insertAll(apiUsers.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching users: ${e.message}")
            }
        }
    }

    /**
     * Get a specific user by its ID from the local cache.
     */
    fun getUserById(id: Int): Flow<User?> = userDao.getUserById(id).map { it?.toDomain() }

    /**
     * Fetches a single user from the API and saves it to the local cache.
     */
    suspend fun refreshUser(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val apiUser = RetrofitInstance.api.getUserById(id)
                userDao.insertAll(listOf(apiUser.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching user $id: ${e.message}")
            }
        }
    }
}
