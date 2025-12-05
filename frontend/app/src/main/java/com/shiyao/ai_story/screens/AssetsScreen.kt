package com.shiyao.ai_story.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.BottomNavBar
import com.shiyao.ai_story.components.CommonCard
import com.shiyao.ai_story.components.CommonTextField
import com.shiyao.ai_story.model.entity.Asset
import com.shiyao.ai_story.model.enums.Status
import com.shiyao.ai_story.navigation.AppRoute
import com.shiyao.ai_story.viewmodel.AssetsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AssetsScreen(
    navController: NavController,
    assetsViewModel: AssetsViewModel
) {
    val assets by assetsViewModel.assetsList.collectAsState()
    val searchText by assetsViewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        assetsViewModel.refreshQuery()
        assetsViewModel.loadAssetsFromRepository()
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            // 标题
            Text(
                text = "StoryFlow",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.text_tertiary),
                modifier = Modifier.padding(top = 18.dp)
            )

            // 搜索框
            CommonTextField(
                placeholder = "Search your stories...",
                value = searchText,
                onValueChange = { assetsViewModel.updateSearchQuery(it) },
                modifier = Modifier.padding(top = 20.dp)
            )

            // 列表
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
            ) {

                items(assets) { asset ->
                    val cover = getAssetCover(asset)
                    val isCompleted =
                        asset.status == Status.COMPLETED.value && asset.videoUrl != null

                    Surface(
                        modifier = Modifier
                            .animateItem()
                            .clickable(
                                enabled = isCompleted,
                                onClick = {
                                    navController.navigate(
                                        AppRoute.previewRoute(
                                            asset.videoUrl!!,
                                            asset.title
                                        )
                                    )
                                }
                            )
                    ) {
                        CommonCard(
                            title = asset.title,
                            tag = Status.from(asset.status).value,
                            imageUrl = cover,
                            content = safeFormatDate(asset.createdAt),
                            imageHeight = 150.dp
                        )
                    }
                }
            }
        }
    }
}

fun safeFormatDate(date: String?): String {
    return try {
        val instant = Instant.parse(date)
        instant.atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
    } catch (e: Exception) {
        Log.e("AssetsScreen", "Error formatting date: $date", e)
        ""
    }
}

@Composable
fun getAssetCover(asset: Asset): Any? {
    val status = Status.from(asset.status)
    return when (status) {
        Status.COMPLETED -> asset.videoUrl ?: R.drawable.placeholder_default
        Status.GENERATING -> null
        Status.FAILED -> R.drawable.placeholder_failed
    }
}
