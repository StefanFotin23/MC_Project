package com.example.mobilecomputingproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobilecomputingproject.ui.screen.details.AlbumDetailScreen
import com.example.mobilecomputingproject.ui.screen.details.CommentDetailScreen
import com.example.mobilecomputingproject.ui.screen.details.PhotoDetailScreen
import com.example.mobilecomputingproject.ui.screen.details.PostDetailScreen
import com.example.mobilecomputingproject.ui.screen.details.TodoDetailScreen
import com.example.mobilecomputingproject.ui.screen.details.UserDetailScreen
import com.example.mobilecomputingproject.ui.screen.list.AlbumListScreen
import com.example.mobilecomputingproject.ui.screen.list.CommentListScreen
import com.example.mobilecomputingproject.ui.screen.list.PhotoListScreen
import com.example.mobilecomputingproject.ui.screen.list.PostListScreen
import com.example.mobilecomputingproject.ui.screen.list.TodoListScreen
import com.example.mobilecomputingproject.ui.screen.list.UserListScreen
import com.example.mobilecomputingproject.ui.screen.ExploreScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.POST_LIST
    ) {
        composable(Routes.POST_LIST) {
            PostListScreen(
                onNavigateToPhotoList = { navController.navigate(Routes.PHOTO_LIST) },
                onNavigateToExplore = { navController.navigate(Routes.EXPLORE) }
            )
        }

        composable(Routes.EXPLORE) {
            ExploreScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.POST_DETAIL) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toInt() ?: 0
            PostDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.COMMENT_LIST) {
            CommentListScreen(
                onCommentClick = { commentId ->
                    navController.navigate("comment_detail/$commentId")
                }
            )
        }

        composable(Routes.COMMENT_DETAIL) { backStackEntry ->
            val commentId = backStackEntry.arguments?.getString("commentId")?.toInt() ?: 0
            CommentDetailScreen(
                commentId = commentId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ALBUM_LIST) {
            AlbumListScreen(
                onAlbumClick = { albumId ->
                    navController.navigate("album_detail/$albumId")
                }
            )
        }

        composable(Routes.ALBUM_DETAIL) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId")?.toInt() ?: 0
            AlbumDetailScreen(
                albumId = albumId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PHOTO_LIST) {
            PhotoListScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.PHOTO_DETAIL) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId")?.toInt() ?: 0
            PhotoDetailScreen(
                photoId = photoId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TODO_LIST) {
            TodoListScreen(
                onTodoClick = { todoId ->
                    navController.navigate("todo_detail/$todoId")
                }
            )
        }

        composable(Routes.TODO_DETAIL) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toInt() ?: 0
            TodoDetailScreen(
                todoId = todoId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.USER_LIST) {
            UserListScreen(
                onUserClick = { userId ->
                    navController.navigate("user_detail/$userId")
                }
            )
        }

        composable(Routes.USER_DETAIL) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: 0
            UserDetailScreen(
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}