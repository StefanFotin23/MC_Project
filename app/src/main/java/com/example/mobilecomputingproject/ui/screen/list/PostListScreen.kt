package com.example.mobilecomputingproject.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WineBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.domain.model.User
import androidx.navigation.NavController
import com.example.mobilecomputingproject.ui.viewmodel.list.PostListViewModel
import com.example.mobilecomputingproject.ui.components.ThemedCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostListViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var authorSearchExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(Color.White)
    ) {
        FilterSection(
            viewModel = viewModel,
            authorSearchExpanded = authorSearchExpanded,
            onAuthorSearchExpandedChange = { authorSearchExpanded = it }
        )
        Column(modifier = Modifier.padding(16.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.posts) { post ->
                    PostItem(
                        post = post,
                        author = uiState.users.find { it.id == post.userId }) {
                        navController.navigate("post_detail/${post.id}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    viewModel: PostListViewModel,
    authorSearchExpanded: Boolean,
    onAuthorSearchExpandedChange: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
        uiState.selectedUser?.let { user ->
            Text(
                text = user.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
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
            val starTint =
                if (uiState.isTopRatedFilterActive) MaterialTheme.colorScheme.primary else Color.Black
            IconButton(onClick = { viewModel.toggleTopRatedFilter() }) {
                Icon(
                    Icons.Default.WineBar,
                    contentDescription = stringResource(R.string.filter_by_top_rated),
                    tint = starTint
                )
            }
            // Favorites Filter
            val favoriteIcon = if (uiState.showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder
            val favoriteTint = if (uiState.showFavoritesOnly) MaterialTheme.colorScheme.primary else Color.Black
            IconButton(onClick = { viewModel.toggleShowFavoritesOnly(!uiState.showFavoritesOnly) }) {
                Icon(favoriteIcon, contentDescription = stringResource(R.string.favorites), tint = favoriteTint)
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
        }
    }
}

@Composable
fun PostItem(post: Post, author: User?, onClick: (Post) -> Unit) {
    ThemedCardItem(onClick = { onClick(post) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
                    .padding(16.dp)
            ) {
                Text(
                    text = post.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (post.isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimary
                )
                author?.let {
                    Text(
                        text = stringResource(R.string.by_author, it.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }),
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.body.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}