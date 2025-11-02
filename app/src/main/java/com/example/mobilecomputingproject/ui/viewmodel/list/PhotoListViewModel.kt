package com.example.mobilecomputingproject.ui.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.AlbumRepository
import com.example.mobilecomputingproject.data.repository.PhotoRepository
import com.example.mobilecomputingproject.data.repository.UserRepository
import com.example.mobilecomputingproject.domain.model.Photo
import com.example.mobilecomputingproject.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoListUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showFavoritesOnly: Boolean = false,
    val users: List<User> = emptyList(), // Add users list for author filter
    val searchQuery: String = "",
    val selectedUser: User? = null
)

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.refreshUsers()
            albumRepository.refreshAlbums()
            photoRepository.refreshPhotos()
        }

        viewModelScope.launch {
            combine(
                photoRepository.getPhotos(),
                userRepository.getUsers(),
                albumRepository.getAlbums(),
                _uiState
            ) { photos, users, albums, uiState ->
                val filteredPhotos = if (uiState.showFavoritesOnly) {
                    photos.filter { it.isFavorite }
                } else {
                    photos
                }

                val authorFilteredPhotos = if (uiState.selectedUser != null) {
                    val userAlbumIds =
                        albums.filter { it.userId == uiState.selectedUser.id }.map { it.id }
                    filteredPhotos.filter { it.albumId in userAlbumIds }
                } else {
                    filteredPhotos
                }

                uiState.copy(photos = authorFilteredPhotos, users = users)
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }

    fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                photoRepository.refreshPhotos()
                // The combine block handles filtering and updating uiState.photos
                // No need to collect photos here directly after refresh
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleFavoriteStatus(photoId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            photoRepository.updateFavoriteStatus(photoId, isFavorite)
        }
    }

    fun toggleShowFavoritesOnly() {
        _uiState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onUserSelected(user: User) {
        _uiState.update { it.copy(selectedUser = user, searchQuery = "") }
        // Refresh photos to apply new filter
        fetchPhotos()
    }

    fun onClearUserFilter() {
        _uiState.update { it.copy(selectedUser = null) }
        // Refresh photos to apply new filter
        fetchPhotos()
    }
}