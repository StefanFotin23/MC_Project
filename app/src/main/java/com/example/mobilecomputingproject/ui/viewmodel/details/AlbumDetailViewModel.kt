package com.example.mobilecomputingproject.ui.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.AlbumRepository
import com.example.mobilecomputingproject.domain.model.Album
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlbumDetailUiState(
    val album: Album? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: AlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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
