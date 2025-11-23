package com.shiyao.ai_story.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shiyao.ai_story.app.MyApplication
import com.shiyao.ai_story.model.repository.ShotRepository
import com.shiyao.ai_story.model.repository.StoryRepository
import com.shiyao.ai_story.screens.AssetsScreen
import com.shiyao.ai_story.screens.CreateScreen
import com.shiyao.ai_story.screens.GenerateStoryScreen
import com.shiyao.ai_story.viewmodel.ShotViewModel
import com.shiyao.ai_story.viewmodel.StoryViewModel
import com.shiyao.ai_story.viewmodel.ViewModelFactory
import okhttp3.internal.platform.PlatformRegistry.applicationContext

/**
 * 应用导航图
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    val database = (applicationContext as MyApplication).database
    val storyDao = database.storyDao()
    val shotDao = database.shotDao()

    val storyRepository = StoryRepository.getInstance(storyDao)
    val shotRepository = ShotRepository.getInstance(shotDao)

    val storyViewModel: StoryViewModel = viewModel(
        factory = ViewModelFactory { StoryViewModel(storyRepository) }
    )

    val shotViewModel: ShotViewModel = viewModel(
        factory = ViewModelFactory { ShotViewModel(shotRepository) }
    )

    NavHost(
        navController = navController,
        startDestination = AppRoute.CREATE.route
    ) {

        composable(AppRoute.CREATE.route) {
            CreateScreen(
                navController = navController,
                storyViewModel = storyViewModel
            )
        }

        composable(AppRoute.ASSETS.route) {
            AssetsScreen(navController)
        }

        composable(AppRoute.GENERATE_STORY.route) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""

            GenerateStoryScreen(
                navController = navController,
                storyId = storyId,
                shotViewModel = shotViewModel,
                storyViewModel = storyViewModel
            )
        }
    }
}
