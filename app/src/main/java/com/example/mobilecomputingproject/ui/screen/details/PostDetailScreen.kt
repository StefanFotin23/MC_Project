package com.example.mobilecomputingproject.ui.screen.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingproject.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.mobilecomputingproject.ui.components.ThemedCardItem
import com.example.mobilecomputingproject.ui.viewmodel.details.PostDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.post_details)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    val post = uiState.post
                    if (post != null) {
                        val favoriteIcon = if (post.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        val favoriteTint = if (post.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        IconButton(onClick = { viewModel.toggleFavoriteStatus(!post.isFavorite) }) {
                            Icon(favoriteIcon, contentDescription = stringResource(R.string.toggle_favorite), tint = favoriteTint)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    val post = uiState.post
                    if (post != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            ThemedCardItem(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
                                        .padding(horizontal = 16.dp, vertical = 24.dp)
                                ) {
                                    Text(
                                        text = post.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            ThemedCardItem(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray.copy(alpha = 0.2f))
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = post.body,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
