package com.example.mobilecomputingproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobilecomputingproject.ui.details.PostDetailScreen
import com.example.mobilecomputingproject.ui.list.PostListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "post_list"
    ) {
        composable("post_list") {
            PostListScreen(
                onPostClick = { postId ->
                    navController.navigate("post_detail/$postId")
                }
            )
        }

        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toInt() ?: 0
            PostDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}