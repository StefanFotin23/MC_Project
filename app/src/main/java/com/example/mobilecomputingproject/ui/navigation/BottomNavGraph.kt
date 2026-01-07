package com.example.mobilecomputingproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobilecomputingproject.ui.screen.ExploreScreen
import com.example.mobilecomputingproject.ui.screen.list.PhotoListScreen
import com.example.mobilecomputingproject.ui.screen.list.PostListScreen

@Composable
fun BottomNavGraph(
    mainNavController: NavHostController,
    bottomNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = bottomNavController,
        startDestination = Routes.POST_LIST,
        modifier = modifier
    ) {
        composable(Routes.POST_LIST) {
            PostListScreen(navController = mainNavController)
        }
        composable(Routes.PHOTO_LIST) {
            PhotoListScreen()
        }
        composable(Routes.EXPLORE) {
            ExploreScreen(navController = mainNavController)
        }

    }
}
