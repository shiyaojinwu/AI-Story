package com.shiyao.ai_story.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonCard
import com.shiyao.ai_story.components.CommonTextField
import com.shiyao.ai_story.model.enums.BottomTab
import com.shiyao.ai_story.navigation.AppRoute
import com.shiyao.ai_story.viewmodel.AssetsViewModel
import com.shiyao.ai_story.viewmodel.StoryViewModel

fun formatDate(rawDate: String?): String {
    if (rawDate.isNullOrEmpty()) return "Unknown Date"
    return try {
        rawDate.split("T")[0]
    } catch (e: Exception) {
        rawDate
    }
}
@Composable
fun AssetsScreen(
    navController: NavController,
    storyViewModel: StoryViewModel,
    assetsViewModel: AssetsViewModel
) {
    val assets by assetsViewModel.assetsList.collectAsState()
    val searchText by assetsViewModel.searchQuery.collectAsState()
    val bottomNavSelected by storyViewModel.bottomNavSelected.collectAsState()

    Scaffold(
        bottomBar = {
            // 底部导航栏
            NavigationBar(containerColor = colorResource(id = R.color.card_background)) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Edit, null) },
                    label = { Text(stringResource(id = R.string.create)) },
                    selected = bottomNavSelected == BottomTab.CREATE,
                    onClick = {
                        storyViewModel.setBottomNavSelected(BottomTab.CREATE)
                        navController.navigate(AppRoute.CREATE.route) { launchSingleTop = true }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Photo, null) },
                    label = { Text(stringResource(id = R.string.assets)) },
                    selected = bottomNavSelected == BottomTab.ASSETS,
                    onClick = { /* 当前页 */ }
                )
            }
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
                modifier = Modifier.padding(top = 16.dp)
            )

            // 搜索框
            CommonTextField(
                placeholder = "Search your stories...",
                value = searchText,
                onValueChange = { assetsViewModel.updateSearchQuery(it) },
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Assets",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.text_secondary),
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
            )

            // 列表 (使用新的AssetCard)
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(assets) { asset ->
                    AssetCard(
                        title = asset.title,
                        date = formatDate(asset.createdAt),
                        imageUrl = asset.thumbnailUrl ?: "",
                        modifier = Modifier.clickable {
                            assetsViewModel.selectAsset(asset)
                            navController.navigate(AppRoute.PREVIEW.route)
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun AssetCard(
    title: String,
    date: String,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Text(
                    text = "Generated",
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.tag_text),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(
                            color = colorResource(id = R.color.tag_background),
                            shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = date,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.text_hint)
                )
            }
        }
    }
}