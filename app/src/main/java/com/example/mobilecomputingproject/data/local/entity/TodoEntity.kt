package com.example.mobilecomputingproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilecomputingproject.domain.model.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean
)

fun TodoEntity.toDomain(): Todo = Todo(userId, id, title, completed)
fun Todo.toEntity(): TodoEntity = TodoEntity(id, userId, title, completed)
