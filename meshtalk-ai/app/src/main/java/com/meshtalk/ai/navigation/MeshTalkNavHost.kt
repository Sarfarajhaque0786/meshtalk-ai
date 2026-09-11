package com.meshtalk.ai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meshtalk.feature.chat.ui.ChatListScreen

/**
 * Top-level navigation graph. As features are added (onboarding, emergency mode,
 * developer dashboard, settings) they register their own destinations here or,
 * for larger surfaces, expose a `xNavGraph(navController)` extension the way
 * `feature:chat` will once it grows past a single screen.
 */
object MeshTalkDestinations {
    const val CHAT_LIST = "chat_list"
}

@Composable
fun MeshTalkNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MeshTalkDestinations.CHAT_LIST
    ) {
        composable(MeshTalkDestinations.CHAT_LIST) {
            ChatListScreen()
        }
    }
}
