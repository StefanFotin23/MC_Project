package com.example.mobilecomputingproject.ui.viewmodel.list

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
import java.util.Collections.emptyList

data class PostListUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PostListViewModel(private val db: AppDatabase) : ViewModel() {

    private val repository = PostRepository(db)

    val uiState: StateFlow<PostListUiState> = repository.getPosts()
        .map { PostListUiState(posts = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PostListUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            repository.refreshPosts()
        }
    }
}