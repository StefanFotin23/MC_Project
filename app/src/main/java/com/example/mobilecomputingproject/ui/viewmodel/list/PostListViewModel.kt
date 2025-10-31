package com.example.mobilecomputingproject.ui.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.PostRepository
import com.example.mobilecomputingproject.data.repository.UserRepository
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostListUiState(
    val posts: List<Post> = emptyList(),
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    val selectedUser: User? = null
)

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            postRepository.refreshPosts()
        }
        viewModelScope.launch {
            userRepository.refreshUsers()
        }

        viewModelScope.launch {
            postRepository.getPosts()
                .map { posts -> posts.sortedByDescending { it.id } }
                .collect { posts ->
                    _uiState.update { it.copy(posts = posts) }
                }
        }

        viewModelScope.launch {
            userRepository.getUsers()
                .collect { users ->
                    _uiState.update { it.copy(users = users) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onUserSelected(user: User) {
        _uiState.update { it.copy(selectedUser = user, searchQuery = "") }
        viewModelScope.launch {
            postRepository.refreshPostsByUser(user.id)
        }
        viewModelScope.launch {
            postRepository.getPostsByUser(user.id)
                .map { posts -> posts.sortedByDescending { it.id } }
                .collect { posts ->
                    _uiState.update { it.copy(posts = posts) }
                }
        }
    }

    fun onClearUserFilter() {
        _uiState.update { it.copy(selectedUser = null) }
        viewModelScope.launch {
            postRepository.refreshPosts()
        }
        viewModelScope.launch {
            postRepository.getPosts()
                .map { posts -> posts.sortedByDescending { it.id } }
                .collect { posts ->
                    _uiState.update { it.copy(posts = posts) }
                }
        }
    }
}
