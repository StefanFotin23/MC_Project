package com.example.mobilecomputingproject.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.CommentRepository
import com.example.mobilecomputingproject.domain.model.Comment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CommentDetailUiState(
    val comment: Comment? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CommentDetailViewModel(
    private val db: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = CommentRepository(db)
    private val commentId: Int = checkNotNull(savedStateHandle["commentId"])

    val uiState: StateFlow<CommentDetailUiState> = repository.getCommentById(commentId)
        .map { CommentDetailUiState(comment = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CommentDetailUiState()
        )

    init {
        viewModelScope.launch {
            repository.refreshComment(commentId)
        }
    }
}
