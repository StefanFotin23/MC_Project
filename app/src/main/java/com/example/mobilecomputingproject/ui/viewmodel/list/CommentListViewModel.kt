package com.example.mobilecomputingproject.ui.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.CommentRepository
import com.example.mobilecomputingproject.domain.model.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentListUiState(
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CommentListViewModel @Inject constructor(
    private val repository: CommentRepository
) : ViewModel() {

    val uiState: StateFlow<CommentListUiState> = repository.getComments()
        .map { CommentListUiState(comments = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CommentListUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            repository.refreshComments()
        }
    }
}
