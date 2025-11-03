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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilecomputingproject.domain.model.Photo
import com.example.mobilecomputingproject.domain.model.Album
import com.example.mobilecomputingproject.domain.model.User
import com.example.mobilecomputingproject.ui.theme.LightningBlue
import com.example.mobilecomputingproject.ui.theme.LightningTurquoise
import com.example.mobilecomputingproject.ui.viewmodel.list.PhotoListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
    viewModel: PhotoListViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var authorSearchExpanded by remember { mutableStateOf(false) }
    var albumSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Photos") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
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
        Text("	Filters", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp)
        Row(horizontalArrangement = Arrangement.End) {
            val favoriteTint =
                if (uiState.showFavoritesOnly) MaterialTheme.colorScheme.primary else Color.Black
            IconButton(onClick = { viewModel.toggleShowFavoritesOnly() }) {
                Icon(
                    if (uiState.showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Filter by Favorites",
                    tint = favoriteTint
                )
            }
            IconButton(onClick = { onAuthorSearchExpandedChange(!authorSearchExpanded) }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Filter by Author",
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
                    placeholder = { Text("Search by author") },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        } else if (uiState.selectedUser != null) {
                            IconButton(onClick = { viewModel.onClearUserFilter() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear filter")
                            }
                        }
                    }
                )
                uiState.users.filter { it.name.contains(uiState.searchQuery, ignoreCase = true) }
                    .forEach { user ->
                        DropdownMenuItem(text = { Text(user.name) }, onClick = {
                            viewModel.onUserSelected(user)
                            onAuthorSearchExpandedChange(false)
                        })
                    }
            }
            // Album Filter
            IconButton(onClick = { onAlbumSearchExpandedChange(!albumSearchExpanded) }) {
                Icon(
                    Icons.Default.Photo,
                    contentDescription = "Filter by Album",
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
                    placeholder = { Text("Search by album") },
                    trailingIcon = {
                        if (uiState.albumSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onAlbumSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        } else if (uiState.selectedAlbum != null) {
                            IconButton(onClick = { viewModel.onClearAlbumFilter() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear filter")
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
                        DropdownMenuItem(text = { Text(album.title) }, onClick = {
                            viewModel.onAlbumSelected(album)
                            onAlbumSearchExpandedChange(false)
                        })
                    }
            }
        }
    }
    // Display selected filters
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        uiState.selectedUser?.let { user ->
            Text(
                text = user.name,
                color = LightningBlue,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
        uiState.selectedAlbum?.let { album ->
            Text(
                text = album.title,
                color = LightningTurquoise,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                textAlign = TextAlign.Center
            )
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
            AsyncImage(
                model = photo.url,
                contentDescription = photo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
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
                    Icon(favoriteIcon, contentDescription = "Toggle Favorite", tint = favoriteTint)
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                author?.let {
                    Text(
                        text = "by ${it.name}",
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                album?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Photo,
                            contentDescription = "Album",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = it.title,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}