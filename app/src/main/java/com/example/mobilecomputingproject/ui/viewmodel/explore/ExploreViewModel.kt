package com.example.mobilecomputingproject.ui.viewmodel.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.AlbumRepository
import com.example.mobilecomputingproject.data.repository.CommentRepository
import com.example.mobilecomputingproject.data.repository.PhotoRepository
import com.example.mobilecomputingproject.data.repository.PostRepository
import com.example.mobilecomputingproject.data.repository.UserRepository
import com.example.mobilecomputingproject.domain.model.Album
import com.example.mobilecomputingproject.domain.model.Photo
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
import javax.inject.Inject

enum class ContentType {
    POSTS,
    PHOTOS
}

data class ExploreUiState(
    val posts: List<Post> = emptyList(),
    val photos: List<Photo> = emptyList(),
    val users: List<User> = emptyList(),
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedContentType: ContentType = ContentType.POSTS
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
    private val albumRepository: AlbumRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadExploreContent()
    }

    fun loadExploreContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Refresh all data to ensure we have the latest favorite statuses
                postRepository.refreshPosts()
                photoRepository.refreshPhotos()
                userRepository.refreshUsers()
                albumRepository.refreshAlbums()
                commentRepository.refreshComments()

                combine(
                    postRepository.getPosts(),
                    photoRepository.getPhotos(),
                    userRepository.getUsers(),
                    albumRepository.getAlbums()
                ) { allPosts, allPhotos, allUsers, allAlbums ->

                    val favoritePostUserIds = allPosts.filter { it.isFavorite }.map { it.userId }.toSet()
                    val favoritePhotoAlbumIds = allPhotos.filter { it.isFavorite }.map { it.albumId }.toSet()
                    val favoritePhotoUserIds = allAlbums.filter { it.id in favoritePhotoAlbumIds }.map { it.userId }.toSet()

                    val relevantUserIds = favoritePostUserIds + favoritePhotoUserIds

                    val explorePosts = allPosts.filter { post ->
                        !post.isFavorite && post.userId in relevantUserIds
                    }

                    val explorePhotos = allPhotos.filter { photo ->
                        val album = allAlbums.find { it.id == photo.albumId }
                        !photo.isFavorite && album?.userId in relevantUserIds
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            posts = explorePosts,
                            photos = explorePhotos,
                            users = allUsers,
                            albums = allAlbums,
                            isLoading = false
                        )
                    }
                }.collect { /* The update is done inside the combine block */ }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleContentType(contentType: ContentType) {
        _uiState.update { it.copy(selectedContentType = contentType) }
    }
}