package com.example.mobilecomputingproject.ui.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.UserRepository
import com.example.mobilecomputingproject.domain.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class UserDetailViewModel(
    private val db: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = UserRepository(db)
    private val userId: Int = checkNotNull(savedStateHandle["userId"])

    val uiState: StateFlow<UserDetailUiState> = repository.getUserById(userId)
        .map { UserDetailUiState(user = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserDetailUiState()
        )

    init {
        viewModelScope.launch {
            repository.refreshUser(userId)
        }
    }
}
