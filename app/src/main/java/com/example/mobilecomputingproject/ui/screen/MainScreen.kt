package com.example.mobilecomputingproject.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputingproject.ui.navigation.BottomNavGraph
import com.example.mobilecomputingproject.ui.navigation.Routes

@Composable
fun MainScreen(
    mainNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController = bottomNavController) }
    ) { paddingValues ->
        BottomNavGraph(
            mainNavController = mainNavController,
            bottomNavController = bottomNavController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavItem.Posts,
        BottomNavItem.Photos,
        BottomNavItem.Explore,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar(containerColor = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                val iconColor = if (selected) MaterialTheme.colorScheme.primary else Color.Black
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                        Icon(screen.icon, contentDescription = screen.title, tint = iconColor)
                    }
                    Text(screen.title, color = iconColor)
                }
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Posts : BottomNavItem(Routes.POST_LIST, "Posts", Icons.Default.Home)
    object Photos : BottomNavItem(Routes.PHOTO_LIST, "Photos", Icons.Default.Photo)
    object Explore : BottomNavItem(Routes.EXPLORE, "Explore", Icons.Default.Explore)
}
