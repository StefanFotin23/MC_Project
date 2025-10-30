package com.example.mobilecomputingproject.ui.list

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
import java.util.Collections.emptyList

data class UserListUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UserListViewModel(private val db: AppDatabase) : ViewModel() {

    private val repository = UserRepository(db)

    val uiState: StateFlow<UserListUiState> = repository.getUsers()
        .map { UserListUiState(users = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserListUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            repository.refreshUsers()
        }
    }
}
