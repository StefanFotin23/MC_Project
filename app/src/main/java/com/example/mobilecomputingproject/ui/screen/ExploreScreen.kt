package com.example.mobilecomputingproject.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobilecomputingproject.domain.model.Photo
import com.example.mobilecomputingproject.domain.model.Post
import com.example.mobilecomputingproject.ui.viewmodel.explore.ContentType
import com.example.mobilecomputingproject.ui.viewmodel.explore.ExploreViewModel
import com.example.mobilecomputingproject.ui.viewmodel.list.PostListViewModel
import com.example.mobilecomputingproject.ui.screen.list.PostDetailPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel(),
    postListViewModel: PostListViewModel = hiltViewModel() // Inject PostListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.clickable {
                            viewModel.loadExploreContent()
                            Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = "Explore", modifier = Modifier.padding(end = 8.dp))
                        Text(text = "Explore")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
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
            verticalArrangement = Arrangement.Top // Align content to top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Posts Filter Box
                val isPostsSelected = uiState.selectedContentType == ContentType.POSTS
                val postsBackgroundBrush = if (isPostsSelected) Brush.linearGradient(listOf(Color(0xFF32527B), Color(0xFF75E9E4))) else null
                val postsTextColor = if (isPostsSelected) Color.White else Color.Black
                val postsBorderBrush = Brush.linearGradient(listOf(Color(0xFF32527B), Color(0xFF75E9E4)))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(postsBackgroundBrush ?: SolidColor(Color.White), RoundedCornerShape(8.dp))
                        .border(1.dp, postsBorderBrush, RoundedCornerShape(8.dp))
                        .clickable { viewModel.toggleContentType(ContentType.POSTS) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Posts", color = postsTextColor, fontWeight = FontWeight.Bold)
                }

                // Photos Filter Box
                val isPhotosSelected = uiState.selectedContentType == ContentType.PHOTOS
                val photosBackgroundBrush = if (isPhotosSelected) Brush.linearGradient(listOf(Color(0xFF32527B), Color(0xFF75E9E4))) else null
                val photosTextColor = if (isPhotosSelected) Color.White else Color.Black
                val photosBorderBrush = Brush.linearGradient(listOf(Color(0xFF32527B), Color(0xFF75E9E4)))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(photosBackgroundBrush ?: SolidColor(Color.White), RoundedCornerShape(8.dp))
                        .border(1.dp, photosBorderBrush, RoundedCornerShape(8.dp))
                        .clickable { viewModel.toggleContentType(ContentType.PHOTOS) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Photos", color = photosTextColor, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else {
                when (uiState.selectedContentType) {
                    ContentType.POSTS -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.posts) { post ->
                                ExplorePostItem(post = post) { selectedPost = post }
                            }
                        }
                    }
                    ContentType.PHOTOS -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.photos) { photo ->
                                ExplorePhotoItem(photo = photo)
                            }
                        }
                    }
                }
            }
        }
    }

    selectedPost?.let { post ->
        PostDetailPopup(postId = post.id, viewModel = postListViewModel) {
            selectedPost = null
        }
    }
}

@Composable
fun ExplorePostItem(post: Post, onClick: (Post) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onClick(post) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = post.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = post.body.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ExplorePhotoItem(photo: Photo) {
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
            Text(
                text = photo.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}