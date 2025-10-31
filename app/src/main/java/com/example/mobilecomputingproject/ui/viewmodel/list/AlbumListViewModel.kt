package com.example.mobilecomputingproject.ui.viewmodel.list

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
import java.util.Collections.emptyList

data class AlbumListUiState(
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AlbumListViewModel(private val db: AppDatabase) : ViewModel() {

    private val repository = AlbumRepository(db)

    val uiState: StateFlow<AlbumListUiState> = repository.getAlbums()
        .map { AlbumListUiState(albums = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AlbumListUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            repository.refreshAlbums()
        }
    }
}
