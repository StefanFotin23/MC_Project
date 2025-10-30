package com.example.mobilecomputingproject.data.repository

import android.util.Log
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.local.toDomain
import com.example.mobilecomputingproject.data.local.toEntity
import com.example.mobilecomputingproject.data.remote.RetrofitInstance
import com.example.mobilecomputingproject.domain.model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TodoRepository(
    private val db: AppDatabase
) {

    private val todoDao = db.todoDao()

    /**
     * Returns a flow of todos from the local cache.
     */
    fun getTodos(): Flow<List<Todo>> = todoDao.getAllTodos().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Fetches todos from the remote API and saves them to the local cache.
     */
    suspend fun refreshTodos() {
        withContext(Dispatchers.IO) {
            try {
                val remoteTodos = RetrofitInstance.api.getTodos()
                Log.d("Repository", "Fetched ${remoteTodos.size} todos from API")
                todoDao.insertAll(remoteTodos.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching todos: ${e.message}")
            }
        }
    }

    /**
     * Get a specific todo by its ID from the local cache.
     */
    fun getTodoById(id: Int): Flow<Todo?> = todoDao.getTodoById(id).map { it?.toDomain() }

    /**
     * Fetches a single todo from the remote API and saves it to the local cache.
     */
    suspend fun refreshTodo(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                val remoteTodo = RetrofitInstance.api.getTodoById(id)
                todoDao.insertAll(listOf(remoteTodo.toEntity()))
            } catch (e: Exception) {
                Log.e("Repository", "Error fetching todo $id: ${e.message}")
            }
        }
    }
}
