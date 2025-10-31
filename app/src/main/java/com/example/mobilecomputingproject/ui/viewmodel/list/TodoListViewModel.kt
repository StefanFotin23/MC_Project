package com.example.mobilecomputingproject.ui.viewmodel.list

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
import java.util.Collections.emptyList

data class TodoListUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TodoListViewModel(private val db: AppDatabase) : ViewModel() {

    private val repository = TodoRepository(db)

    val uiState: StateFlow<TodoListUiState> = repository.getTodos()
        .map { TodoListUiState(todos = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TodoListUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            repository.refreshTodos()
        }
    }
}
