package com.example.mobilecomputingproject.ui.details

import androidx.lifecycle.SavedStateHandle
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

data class PhotoDetailUiState(
    val photo: Photo? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class PhotoDetailViewModel(
    private val db: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = PhotoRepository(db)
    private val photoId: Int = checkNotNull(savedStateHandle["photoId"])

    val uiState: StateFlow<PhotoDetailUiState> = repository.getPhotoById(photoId)
        .map { PhotoDetailUiState(photo = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PhotoDetailUiState()
        )

    init {
        viewModelScope.launch {
            repository.refreshPhoto(photoId)
        }
    }
}
