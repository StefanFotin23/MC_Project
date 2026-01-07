package com.example.mobilecomputingproject.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.domain.model.Photo
import com.example.mobilecomputingproject.domain.model.Album
import com.example.mobilecomputingproject.domain.model.User
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.ui.res.stringResource
import com.example.mobilecomputingproject.ui.viewmodel.list.PhotoListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    viewModel: PhotoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var authorSearchExpanded by remember { mutableStateOf(false) }
    var albumSearchExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PhotoFilterSection(
            viewModel = viewModel,
            authorSearchExpanded = authorSearchExpanded,
            onAuthorSearchExpandedChange = { authorSearchExpanded = it },
            albumSearchExpanded = albumSearchExpanded,
            onAlbumSearchExpandedChange = { albumSearchExpanded = it }
        )
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.error != null) {
            Text(text = stringResource(R.string.error_prefix) + uiState.error, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.photos) { photo ->
                    val album = uiState.albums.find { it.id == photo.albumId }
                    val author = uiState.users.find { it.id == album?.userId }
                    PhotoItem(photo = photo, album = album, author = author, viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoFilterSection(
    viewModel: PhotoListViewModel,
    authorSearchExpanded: Boolean,
    onAuthorSearchExpandedChange: (Boolean) -> Unit,
    albumSearchExpanded: Boolean,
    onAlbumSearchExpandedChange: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(75.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(R.string.filters), color = Color.Black, style = MaterialTheme.typography.titleMedium)

        if (uiState.selectedUser != null) {
            Text(
                text = uiState.selectedUser!!.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(
                    brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                )
            )
        } else if (uiState.selectedAlbum != null) {
            Text(
                text = uiState.selectedAlbum!!.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(
                    brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                )
            )
        }

        Row(horizontalArrangement = Arrangement.End) {
            val favoriteTint =
                if (uiState.showFavoritesOnly) MaterialTheme.colorScheme.primary else Color.Black
            IconButton(onClick = { viewModel.toggleShowFavoritesOnly() }) {
                Icon(
                    if (uiState.showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.filter_by_favorites),
                    tint = favoriteTint
                )
            }
            IconButton(onClick = { onAuthorSearchExpandedChange(!authorSearchExpanded) }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = stringResource(R.string.filter_by_author),
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = authorSearchExpanded,
                onDismissRequest = { onAuthorSearchExpandedChange(false) }
            ) {
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text(stringResource(R.string.search_by_author)) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search))
                            }
                        } else if (uiState.selectedUser != null) {
                            IconButton(onClick = { viewModel.onClearUserFilter() }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_filter))
                            }
                        }
                    }
                )
                uiState.users.filter { it.name.contains(uiState.searchQuery, ignoreCase = true) }
                    .forEach { user ->
                        DropdownMenuItem(text = { Text(user.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) }, onClick = {
                            viewModel.onUserSelected(user)
                            onAuthorSearchExpandedChange(false)
                        })
                    }
            }
            // Album Filter
            IconButton(onClick = { onAlbumSearchExpandedChange(!albumSearchExpanded) }) {
                Icon(
                    Icons.Default.Photo,
                    contentDescription = stringResource(R.string.filter_by_album),
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = albumSearchExpanded,
                onDismissRequest = { onAlbumSearchExpandedChange(false) }
            ) {
                TextField(
                    value = uiState.albumSearchQuery,
                    onValueChange = { viewModel.onAlbumSearchQueryChanged(it) },
                    placeholder = { Text(stringResource(R.string.search_by_album)) },
                    trailingIcon = {
                        if (uiState.albumSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onAlbumSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search))
                            }
                        } else if (uiState.selectedAlbum != null) {
                            IconButton(onClick = { viewModel.onClearAlbumFilter() }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_filter))
                            }
                        }
                    }
                )
                uiState.albums.filter {
                    it.title.contains(
                        uiState.albumSearchQuery,
                        ignoreCase = true
                    )
                }
                    .forEach { album ->
                        DropdownMenuItem(text = { Text(album.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) }, onClick = {
                            viewModel.onAlbumSelected(album)
                            onAlbumSearchExpandedChange(false)
                        })
                    }
            }
            val isFilterActive = uiState.selectedUser != null || uiState.selectedAlbum != null
            val clearIconTint = if (isFilterActive) Color.Red else Color.Black
            IconButton(onClick = { viewModel.clearFilters() }) {
                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_filters), tint = clearIconTint)
            }
        }
    }
}

@Composable
fun PhotoItem(photo: Photo, album: Album?, author: User?, viewModel: PhotoListViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SubcomposeAsyncImage(
                model = photo.url,
                contentDescription = photo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                },
                error = {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = stringResource(R.string.photo_placeholder),
                        modifier = Modifier.size(48.dp)
                    )
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = photo.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                val favoriteIcon =
                    if (photo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                val favoriteTint =
                    if (photo.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                IconButton(onClick = {
                    viewModel.toggleFavoriteStatus(
                        photo.id,
                        !photo.isFavorite
                    )
                }) {
                    Icon(favoriteIcon, contentDescription = stringResource(R.string.toggle_favorite), tint = favoriteTint)
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                author?.let {
                    Text(
                        text = stringResource(R.string.by_author, it.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }),
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                album?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Photo,
                            contentDescription = stringResource(R.string.album),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = it.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}