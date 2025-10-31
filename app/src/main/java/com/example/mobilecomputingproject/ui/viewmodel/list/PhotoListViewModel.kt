package com.example.mobilecomputingproject.ui.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.data.repository.PhotoRepository
import com.example.mobilecomputingproject.domain.model.Photo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

data class PhotoListUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PhotoListViewModel(private val db: AppDatabase) : ViewModel() {

    private val repository = PhotoRepository(db)

    val uiState: StateFlow<PhotoListUiState> = repository.getPhotos()
        .map { PhotoListUiState(photos = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PhotoListUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            repository.refreshPhotos()
        }
    }
}
