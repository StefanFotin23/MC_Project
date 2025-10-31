package com.example.mobilecomputingproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputingproject.ui.navigation.NavGraph
import com.example.mobilecomputingproject.ui.theme.LightningRed
import com.example.mobilecomputingproject.ui.theme.LightningYellow
import com.example.mobilecomputingproject.ui.theme.MobileComputingProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MobileComputingProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(LightningRed, LightningYellow))),
                    // Apply gradient background
                    contentColor = Color.White // Set content color for better contrast
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}