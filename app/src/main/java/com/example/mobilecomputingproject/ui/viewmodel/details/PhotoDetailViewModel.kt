package com.example.mobilecomputingproject.ui.viewmodel.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingproject.data.repository.PhotoRepository
import com.example.mobilecomputingproject.domain.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoDetailUiState(
    val photo: Photo? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val repository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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
