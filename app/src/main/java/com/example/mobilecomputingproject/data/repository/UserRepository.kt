package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.toDomain
import com.example.mobilecomputingproject.data.local.toEntity
import com.example.mobilecomputingproject.data.remote.RetrofitInstance
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
     * Fetches users from the remote API and saves them to the local cache.
     */
    suspend fun refreshUsers() {
        withContext(Dispatchers.IO) {
            try {
                val remoteUsers = RetrofitInstance.api.getUsers()
                Log.d("Repository", "Fetched ${remoteUsers.size} users from API")
                userDao.insertAll(remoteUsers.map { it.toEntity() })
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
     * Fetches a single user from the remote API and saves it to the local cache.
     */
    suspend fun refreshUser(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val remoteUser = RetrofitInstance.api.getUserById(id)
                userDao.insertAll(listOf(remoteUser.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching user $id: ${e.message}")
            }
        }
    }
}
