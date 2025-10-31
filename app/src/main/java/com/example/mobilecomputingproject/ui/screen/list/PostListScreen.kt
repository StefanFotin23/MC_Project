package com.example.mobilecomputingproject.ui.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.domain.model.User
import com.example.mobilecomputingproject.ui.viewmodel.list.PostListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var authorSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    Box {
                        TextField(
                            value = uiState.searchQuery,
                            onValueChange = {
                                viewModel.onSearchQueryChanged(it)
                                authorSearchExpanded = it.isNotEmpty()
                            },
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
                                                    }                        )
                        DropdownMenu(
                            expanded = authorSearchExpanded,
                            onDismissRequest = { authorSearchExpanded = false }
                        ) {
                            uiState.users.filter { it.name.contains(uiState.searchQuery, ignoreCase = true) }.forEach { user ->
                                DropdownMenuItem(text = { Text(user.name) }, onClick = { 
                                    viewModel.onUserSelected(user)
                                    authorSearchExpanded = false
                                })
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* Already on Posts screen */ }) {
                        Icon(Icons.Default.Home, contentDescription = "Posts")
                    }
                    Text("Posts")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /*TODO: Navigate to Favorites screen*/ }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                    Text("Favorites")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(uiState.posts) { post ->
                PostItem(post = post, author = uiState.users.find { it.id == post.userId })
            }
        }
    }
}

@Composable
fun PostItem(post: Post, author: User?) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post.title)
            author?.let {
                Text(text = "by ${it.name}")
            }
            Text(text = post.body)
        }
    }
}
