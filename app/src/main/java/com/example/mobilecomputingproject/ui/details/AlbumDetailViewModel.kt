package com.example.mobilecomputingproject.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.AlbumRepository
import com.example.mobilecomputingproject.domain.model.Album
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AlbumDetailUiState(
    val album: Album? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class AlbumDetailViewModel(
    private val db: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = AlbumRepository(db)
    private val albumId: Int = checkNotNull(savedStateHandle["albumId"])

    val uiState: StateFlow<AlbumDetailUiState> = repository.getAlbumById(albumId)
        .map { AlbumDetailUiState(album = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AlbumDetailUiState()
        )

    init {
        viewModelScope.launch {
            repository.refreshAlbum(albumId)
        }
    }
}
