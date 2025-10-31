package com.example.mobilecomputingproject.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingproject.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        "Posts",
        "Comments",
        "Albums",
        "Photos",
        "Todos",
        "Users"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mobile Computing Project") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onNavigate(getRouteForItem(item)) }
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

private fun getRouteForItem(item: String): String {
    return when (item) {
        "Posts" -> Routes.POST_LIST
        "Comments" -> Routes.COMMENT_LIST
        "Albums" -> Routes.ALBUM_LIST
        "Photos" -> Routes.PHOTO_LIST
        "Todos" -> Routes.TODO_LIST
        "Users" -> Routes.USER_LIST
        else -> ""
    }
}
