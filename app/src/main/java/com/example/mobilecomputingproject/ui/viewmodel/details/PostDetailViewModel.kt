package com.example.mobilecomputingproject.ui.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.PostRepository
import com.example.mobilecomputingproject.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailUiState(
    val post: Post? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val repository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = (savedStateHandle.get<String>("postId")!!).toInt()

    val uiState: StateFlow<PostDetailUiState> = repository.getPostById(postId)
        .map { post ->
            PostDetailUiState(post = post, isLoading = false)
        }
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

    fun toggleFavoriteStatus(isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(postId, isFavorite)
        }
    }
}