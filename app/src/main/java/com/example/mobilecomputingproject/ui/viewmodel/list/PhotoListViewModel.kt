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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mobilecomputingproject.domain.model.Album

data class PhotoFilters(
    val showFavoritesOnly: Boolean = false,
    val selectedUser: User? = null,
    val selectedAlbum: Album? = null
)

data class PhotoListUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showFavoritesOnly: Boolean = false,
    val users: List<User> = emptyList(), // Add users list for author filter
    val albums: List<Album> = emptyList(), // Add albums list for album filter
    val searchQuery: String = "", // For author search
    val selectedUser: User? = null,
    val albumSearchQuery: String = "", // For album search
    val selectedAlbum: Album? = null
)

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState> = _uiState.asStateFlow()

    private val _filters = MutableStateFlow(PhotoFilters())

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
                _filters
            ) { photos, users, albums, filters ->
                val filteredPhotos = if (filters.showFavoritesOnly) {
                    photos.filter { it.isFavorite }
                } else {
                    photos
                }

                val authorFilteredPhotos = if (filters.selectedUser != null) {
                    val userAlbumIds =
                        albums.filter { it.userId == filters.selectedUser.id }.map { it.id }
                    filteredPhotos.filter { it.albumId in userAlbumIds }
                } else {
                    filteredPhotos
                }

                val albumFilteredPhotos = if (filters.selectedAlbum != null) {
                    authorFilteredPhotos.filter { it.albumId == filters.selectedAlbum.id }
                } else {
                    authorFilteredPhotos
                }

                _uiState.update { it.copy(
                    photos = albumFilteredPhotos,
                    users = users,
                    albums = albums,
                    isLoading = false,
                    selectedUser = filters.selectedUser,
                    selectedAlbum = filters.selectedAlbum,
                    showFavoritesOnly = filters.showFavoritesOnly
                ) }
            }.collect()
        }
    }

    fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                photoRepository.refreshPhotos()
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
        _filters.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onUserSelected(user: User) {
        _filters.update { it.copy(selectedUser = user, selectedAlbum = null) }
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun onClearUserFilter() {
        _filters.update { it.copy(selectedUser = null) }
    }

    fun onAlbumSearchQueryChanged(query: String) {
        _uiState.update { it.copy(albumSearchQuery = query) }
    }

    fun onAlbumSelected(album: Album) {
        _filters.update { it.copy(selectedAlbum = album, selectedUser = null) }
        _uiState.update { it.copy(albumSearchQuery = "") }
    }

    fun onClearAlbumFilter() {
        _filters.update { it.copy(selectedAlbum = null) }
    }

    fun clearFilters() {
        _filters.update { it.copy(selectedUser = null, selectedAlbum = null) }
    }
}