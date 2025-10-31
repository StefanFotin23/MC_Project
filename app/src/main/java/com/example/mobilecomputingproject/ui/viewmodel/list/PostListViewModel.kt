package com.example.mobilecomputingproject.ui.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.CommentRepository
import com.example.mobilecomputingproject.data.repository.PostRepository
import com.example.mobilecomputingproject.data.repository.UserRepository
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.collections.map

data class PostListUiState(
    val posts: List<Post> = emptyList(),
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    val selectedUser: User? = null,
    val isTopRatedFilterActive: Boolean = false,
    val showFavoritesOnly: Boolean = false
)

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    private suspend fun getPostsWithCommentCounts(posts: List<Post>): List<Pair<Post, Int>> = coroutineScope {
        posts.map { post ->
            async { post to commentRepository.getCommentCountForPost(post.id).first() }
        }.awaitAll()
    }

    init {
        viewModelScope.launch {
            postRepository.refreshPosts()
            userRepository.refreshUsers()
            // Refresh comments as well
            commentRepository.refreshComments()
        }

        viewModelScope.launch {
            combine(
                postRepository.getPosts(),
                userRepository.getUsers(),
                _uiState
            ) { posts, users, uiState ->
                val filteredPosts = if (uiState.selectedUser != null) {
                    posts.filter { it.userId == uiState.selectedUser.id }
                } else {
                    posts
                }

                val favoriteFilteredPosts = if (uiState.showFavoritesOnly) {
                    filteredPosts.filter { it.isFavorite }
                } else {
                    filteredPosts
                }

                val sortedPosts = if (uiState.isTopRatedFilterActive) {
                    val postsWithCommentCount = getPostsWithCommentCounts(favoriteFilteredPosts)
                    postsWithCommentCount.sortedByDescending { it.second }.map { it.first }
                } else {
                    favoriteFilteredPosts.sortedByDescending { it.id }
                }

                uiState.copy(posts = sortedPosts, users = users)
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }

    fun toggleTopRatedFilter() {
        _uiState.update { it.copy(isTopRatedFilterActive = !it.isTopRatedFilterActive) }
    }

    fun toggleShowFavoritesOnly() {
        _uiState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onUserSelected(user: User) {
        _uiState.update { it.copy(selectedUser = user, searchQuery = "") }
        viewModelScope.launch {
            postRepository.refreshPostsByUser(user.id)
            commentRepository.refreshComments() // Ensure comments are refreshed for sorting

            postRepository.getPostsByUser(user.id)
                .collect { posts ->
                    val favoriteFilteredPosts = if (_uiState.value.showFavoritesOnly) {
                        posts.filter { it.isFavorite }
                    } else {
                        posts
                    }
                    val sortedPosts = if (_uiState.value.isTopRatedFilterActive) {
                        val postsWithCommentCount = getPostsWithCommentCounts(favoriteFilteredPosts)
                        postsWithCommentCount.sortedByDescending { it.second }.map { it.first }
                    } else {
                        favoriteFilteredPosts.sortedByDescending { it.id }
                    }
                    _uiState.update { it.copy(posts = sortedPosts) }
                }
        }
    }

    fun onClearUserFilter() {
        _uiState.update { it.copy(selectedUser = null) }
        viewModelScope.launch {
            postRepository.refreshPosts()
            commentRepository.refreshComments() // Ensure comments are refreshed for sorting

            postRepository.getPosts()
                .collect { posts ->
                    val favoriteFilteredPosts = if (_uiState.value.showFavoritesOnly) {
                        posts.filter { it.isFavorite }
                    } else {
                        posts
                    }
                    val sortedPosts = if (_uiState.value.isTopRatedFilterActive) {
                        val postsWithCommentCount = getPostsWithCommentCounts(favoriteFilteredPosts)
                        postsWithCommentCount.sortedByDescending { it.second }.map { it.first }
                    } else {
                        favoriteFilteredPosts.sortedByDescending { it.id }
                    }
                    _uiState.update { it.copy(posts = sortedPosts) }
                }
        }
    }

    fun toggleFavoriteStatus(postId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            postRepository.updateFavoriteStatus(postId, isFavorite)
        }
    }
}
