package com.shiyao.ai_story.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shiyao.ai_story.app.MyApplication
import com.shiyao.ai_story.model.repository.AssetRepository
import com.shiyao.ai_story.model.repository.ShotRepository
import com.shiyao.ai_story.model.repository.StoryRepository
import com.shiyao.ai_story.screens.AssetsScreen
import com.shiyao.ai_story.screens.CreateScreen
import com.shiyao.ai_story.screens.GenerateStoryScreen
import com.shiyao.ai_story.screens.PreviewScreen
import com.shiyao.ai_story.screens.ShotDetailScreen
import com.shiyao.ai_story.viewmodel.AssetsViewModel
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

    // 共享的 ShotViewModel
    val shotViewModel: ShotViewModel = viewModel(
        factory = ViewModelFactory { ShotViewModel(shotRepository) }
    )

    val assetDao = database.assetDao()

    // 初始化 Repository
    val assetRepository = AssetRepository.getInstance(assetDao)

    // 初始化 ViewModel
    val assetsViewModel: AssetsViewModel = viewModel(
        factory = ViewModelFactory { AssetsViewModel(assetRepository) }
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
            AssetsScreen(
                navController = navController,
                storyViewModel = storyViewModel,
                assetsViewModel = assetsViewModel
            )
        }

        // 3. 预览页 (PreviewScreen) - 正确传递 shotViewModel
        composable(AppRoute.PREVIEW.route)
        { backStackEntry ->
            val assetName = backStackEntry.arguments?.getString("assetName") ?: ""
            PreviewScreen(
                navController = navController,
                assetName = assetName,
                shotViewModel = shotViewModel // 修复：正确传递参数
            )
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

        // 分镜详情页路由
        composable(AppRoute.SHOT_DETAIL.route) {
            ShotDetailScreen(
                navController = navController,
                shotViewModel = shotViewModel
            )
        }
    }
}