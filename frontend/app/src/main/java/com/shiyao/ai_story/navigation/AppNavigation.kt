package com.shiyao.ai_story.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shiyao.ai_story.screens.AssetsScreen
import com.shiyao.ai_story.screens.CreateScreen

/**
 * 应用导航图
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.CREATE.route
    ) {
        composable(route = AppRoute.CREATE.route) {
            CreateScreen(navController = navController)
        }

        composable(route = AppRoute.ASSETS.route) {
            AssetsScreen(navController = navController)
        }
    }
}
