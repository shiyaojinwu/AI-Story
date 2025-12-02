package com.shiyao.ai_story.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.navigation.AppRoute

@Composable
fun BottomNavBar(
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = colorResource(id = R.color.card_background)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            label = { Text(stringResource(id = R.string.create)) },
            selected = currentRoute in listOf(
                AppRoute.CREATE.route,
                AppRoute.GENERATE_STORY.route,
                AppRoute.SHOT_DETAIL.route,
            ),
            onClick = {
                if (currentRoute != AppRoute.CREATE.route) {
                    navController.navigate(AppRoute.CREATE.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Photo, contentDescription = null) },
            label = { Text(stringResource(id = R.string.assets)) },
            selected = currentRoute in listOf(
                AppRoute.ASSETS.route,
            ),
            onClick = {
                if (currentRoute != AppRoute.ASSETS.route) {
                    navController.navigate(AppRoute.ASSETS.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            }
        )
    }
}
