package com.example.mobilecomputingproject.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mobilecomputingproject.data.local.AppDatabase
import com.example.mobilecomputingproject.ui.viewmodel.details.AlbumDetailViewModel
import com.example.mobilecomputingproject.ui.viewmodel.details.CommentDetailViewModel
import com.example.mobilecomputingproject.ui.viewmodel.details.PhotoDetailViewModel
import com.example.mobilecomputingproject.ui.viewmodel.details.PostDetailViewModel
import com.example.mobilecomputingproject.ui.viewmodel.details.TodoDetailViewModel
import com.example.mobilecomputingproject.ui.viewmodel.details.UserDetailViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.AlbumListViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.CommentListViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.PhotoListViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.PostListViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.TodoListViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.UserListViewModel

class ViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(PostListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostListViewModel(db) as T
        }
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return PostDetailViewModel(db, savedStateHandle) as T
        }
        if (modelClass.isAssignableFrom(CommentListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentListViewModel(db) as T
        }
        if (modelClass.isAssignableFrom(CommentDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return CommentDetailViewModel(db, savedStateHandle) as T
        }
        if (modelClass.isAssignableFrom(AlbumListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumListViewModel(db) as T
        }
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return AlbumDetailViewModel(db, savedStateHandle) as T
        }
        if (modelClass.isAssignableFrom(PhotoListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoListViewModel(db) as T
        }
        if (modelClass.isAssignableFrom(PhotoDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return PhotoDetailViewModel(db, savedStateHandle) as T
        }
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoListViewModel(db) as T
        }
        if (modelClass.isAssignableFrom(TodoDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return TodoDetailViewModel(db, savedStateHandle) as T
        }
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserListViewModel(db) as T
        }
        if (modelClass.isAssignableFrom(UserDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return UserDetailViewModel(db, savedStateHandle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
