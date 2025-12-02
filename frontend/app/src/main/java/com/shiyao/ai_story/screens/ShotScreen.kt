package com.shiyao.ai_story.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonCard
import com.shiyao.ai_story.model.enums.ShotStatus
import com.shiyao.ai_story.navigation.AppRoute
import com.shiyao.ai_story.utils.ToastUtils
import com.shiyao.ai_story.viewmodel.ShotViewModel
import com.shiyao.ai_story.viewmodel.StoryViewModel
import com.shiyao.ai_story.viewmodel.UIState

/**
 * 分镜生成与视频创建屏幕
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShotScreen(
    navController: NavController,
    storyId: String?,
    shotViewModel: ShotViewModel,
    storyViewModel: StoryViewModel
) {
    val shots = shotViewModel.shots.collectAsState()
    val allCompleted = shots.value.all { it.status == ShotStatus.COMPLETED.value }
    val storyTitle = storyViewModel.storyTitle.collectAsState()
    val allShotsCompletedOrFail = shotViewModel.allShotsCompletedOrFail.collectAsState()
    val context = LocalContext.current
    val generateVideoState by storyViewModel.generateVideoState.collectAsState()
    val videoProgress by storyViewModel.videoProgress.collectAsState()
    val isLoadingVideo = generateVideoState is UIState.Loading

    if (storyId == null) {
        navController.popBackStack()
        return
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { shots.value.size.coerceAtLeast(1) }
    )

    DisposableEffect(Unit) {
        val title = storyViewModel.storyTitle.value
        shotViewModel.pollShotsUntilCompleted(storyId, title)

        onDispose {
            shotViewModel.stopPolling()
            shotViewModel.refreshShots()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        // 顶部栏：只显示 Back，不显示 StoryFlow 标题
        StoryTopBar(
            showBack = true,
            showTitle = false,
            onBack = { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.padding(bottom = 16.dp))

        // 副标题和故事标题
        Text(
            text = "Storyboard",
            color = colorResource(id = R.color.text_secondary),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = storyTitle.value,
            fontSize = 18.sp,
            color = colorResource(id = R.color.text_hint),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (shots.value.isEmpty()) {
                    Text(
                        text = "Loading shots...",
                        color = Color.Gray,
                        modifier = Modifier.padding(20.dp)
                    )
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) { page ->
                        val shot = shots.value[page]
                        val status = ShotStatus.from(shot.status)

                        when (status) {
                            ShotStatus.COMPLETED -> {
                                // completed：正常显示，如果 URL 为空则在卡片内显示 Loading 图片
                                CommonCard(
                                    title = shot.title,
                                    tag = shot.status,
                                    content = shot.prompt,
                                    imageUrl = shot.imageUrl,
                                    backgroundColor = colorResource(id = R.color.card_background),
                                    showLoadingWhenNoImage = true,
                                    // 添加点击事件,只有所有分镜都停止生成时才允许点击
                                    modifier = if (allShotsCompletedOrFail.value) {
                                        Modifier.clickable {
                                            navController.navigate(AppRoute.shotDetailRoute(shot.id))
                                        }
                                    } else {
                                        Modifier
                                    }
                                )
                            }

                            ShotStatus.GENERATING -> {
                                // generating：始终显示 Loading 图片
                                CommonCard(
                                    title = shot.title,
                                    tag = shot.status,
                                    content = shot.prompt,
                                    imageUrl = null,
                                    backgroundColor = colorResource(id = R.color.card_background),
                                    showLoadingWhenNoImage = true
                                )
                            }



                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "生成失败",
                                        color = Color.Red.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CommonButton(
            text = "Generate Video",
            backgroundColor = colorResource(id = R.color.primary),
            contentColor = Color.White,
            fontSize = 25,
            horizontalPadding = 16,
            verticalPadding = 16,
            enabled = shots.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (!allCompleted) {
                    ToastUtils.showShort(context, "Please wait for all shots to be completed")
                    return@CommonButton
                }

                // 全部生成完成，生成视频（POST /api/story/{id}/generate-video）
                storyViewModel.generateVideo(storyId)
                navController.navigate(AppRoute.PREVIEW.route)
            }
        )
    }
    // Loading 弹窗
    if (isLoadingVideo) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.primary),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                Text(
                    text = if (videoProgress != null) {
                        "Loading... $videoProgress%"
                    } else {
                        "Loading..."
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}