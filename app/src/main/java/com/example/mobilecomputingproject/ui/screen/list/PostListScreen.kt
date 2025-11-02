package com.example.mobilecomputingproject.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.domain.model.User
import com.example.mobilecomputingproject.ui.navigation.Routes
import com.example.mobilecomputingproject.ui.theme.LightningBlue
import com.example.mobilecomputingproject.ui.theme.LightningTurquoise
import com.example.mobilecomputingproject.ui.viewmodel.list.PostListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    viewModel: PostListViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var authorSearchExpanded by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                modifier = Modifier.background(Color.White),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            viewModel.refreshPosts()
                            Toast.makeText(context, "Refreshing posts...", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Home, contentDescription = "Posts")
                        }
                        Text("Posts")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { navController.navigate(Routes.PHOTO_LIST) }) {
                            Icon(Icons.Default.Photo, contentDescription = "Photos")
                        }
                        Text("Photos")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val favoriteIcon = if (uiState.showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        val favoriteTint = if (uiState.showFavoritesOnly) MaterialTheme.colorScheme.primary else Color.Black
                        IconButton(onClick = { viewModel.toggleShowFavoritesOnly() }) {
                            Icon(favoriteIcon, contentDescription = "Favorites", tint = favoriteTint)
                        }
                        Text("Favorites")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
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
                            selectedPost = post
                        }
                    }
                }
            }
        }
    }

    selectedPost?.let { post ->
        PostDetailPopup(postId = post.id, viewModel = viewModel) {
            selectedPost = null
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
            val starTint =
                if (uiState.isTopRatedFilterActive) MaterialTheme.colorScheme.primary else Color.Black
            IconButton(onClick = { viewModel.toggleTopRatedFilter() }) {
                Icon(
                    Icons.Default.WineBar,
                    contentDescription = "Filter by Top Rated",
                    tint = starTint
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
        }
    }
}

@Composable
fun PostItem(post: Post, author: User?, onClick: (Post) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable { onClick(post) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(LightningBlue, LightningTurquoise)))
                    .padding(16.dp)
            ) {
                Text(
                    text = post.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                author?.let {
                    Text(
                        text = "by ${it.name}",
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
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

@Composable
fun PostDetailPopup(postId: Int, viewModel: PostListViewModel, onClose: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val post = uiState.posts.find { it.id == postId } ?: return // Return if post not found

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f) // Allow text to take available space
                    )
                    Row {
                        val favoriteIcon = if (post.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        val favoriteTint = if (post.isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimary
                        IconButton(onClick = { viewModel.toggleFavoriteStatus(post.id, !post.isFavorite) }) {
                            Icon(favoriteIcon, contentDescription = "Toggle Favorite", tint = favoriteTint)
                        }
                        IconButton(
                            onClick = onClose
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
                // TODO: Clarify image source for post detail popup
                AsyncImage(
                    model = "https://via.placeholder.com/400", // Placeholder image
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(text = post.body.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                }
            }
        }
    }
}