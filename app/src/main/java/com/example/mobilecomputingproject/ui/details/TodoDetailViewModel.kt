package com.example.mobilecomputingproject.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.TodoRepository
import com.example.mobilecomputingproject.domain.model.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodoDetailUiState(
    val todo: Todo? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class TodoDetailViewModel(
    private val db: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = TodoRepository(db)
    private val todoId: Int = checkNotNull(savedStateHandle["todoId"])

    val uiState: StateFlow<TodoDetailUiState> = repository.getTodoById(todoId)
        .map { TodoDetailUiState(todo = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TodoDetailUiState()
        )

    init {
        viewModelScope.launch {
            repository.refreshTodo(todoId)
        }
    }
}
