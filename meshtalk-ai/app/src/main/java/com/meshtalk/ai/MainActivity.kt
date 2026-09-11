package com.meshtalk.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.meshtalk.ai.navigation.MeshTalkNavHost
import com.meshtalk.core.designsystem.theme.MeshTalkTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity host. All screens are Composable destinations wired through
 * [MeshTalkNavHost]; MainActivity itself contains no business logic.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeshTalkTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    MeshTalkNavHost(navController = navController)
                }
            }
        }
    }
}
