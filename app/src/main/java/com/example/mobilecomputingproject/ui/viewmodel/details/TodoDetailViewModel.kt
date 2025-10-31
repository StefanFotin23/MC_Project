package com.example.mobilecomputingproject.ui.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.TodoRepository
import com.example.mobilecomputingproject.domain.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoDetailUiState(
    val todo: Todo? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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
