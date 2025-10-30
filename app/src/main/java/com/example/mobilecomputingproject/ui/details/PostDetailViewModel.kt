package com.example.mobilecomputingproject.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.PostRepository
import com.example.mobilecomputingproject.domain.model.Post
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PostDetailUiState(
    val post: Post? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class PostDetailViewModel(
    private val db: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = PostRepository(db)
    private val postId: Int = (savedStateHandle.get<String>("postId")!!).toInt()

    val uiState: StateFlow<PostDetailUiState> = repository.getPostById(postId)
        .map { PostDetailUiState(post = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PostDetailUiState()
        )

    init {
        viewModelScope.launch {
            repository.refreshPost(postId)
        }
    }
}