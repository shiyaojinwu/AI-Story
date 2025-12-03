package com.shiyao.ai_story.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonCard
import com.shiyao.ai_story.components.CommonLoadingOverlay
import com.shiyao.ai_story.components.LoadingType
import com.shiyao.ai_story.components.TopBackBar
import com.shiyao.ai_story.model.enums.Status
import com.shiyao.ai_story.model.ui.ShotUI
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
    val allCompleted = shots.value.all { it.status == Status.COMPLETED.value }
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
        pageCount = { shots.value.size }
    )

    // 监听生成视频状态：成功后再跳转预览页；失败时给出提示
    LaunchedEffect(generateVideoState) {
        if (generateVideoState.isSuccess) {
            // 生成成功才跳转预览页
            navController.navigate(AppRoute.PREVIEW.route)
            storyViewModel.clearGenerateVideoState()
        } else if (generateVideoState.isError) {
            val message = (generateVideoState as UIState.Error).message ?: "生成视频失败"
            ToastUtils.showLong(context, message)
        }
    }

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // 顶部栏：只显示 Back，不显示 StoryFlow 标题
        TopBackBar(onBack = { navController.popBackStack() })

        Spacer(modifier = Modifier.padding(bottom = 16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // 副标题和故事标题
            Text(
                text = "Storyboard",
                color = colorResource(id = R.color.text_secondary),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp)

            )

            Text(
                text = storyTitle.value,
                fontSize = 36.sp,
                color = colorResource(id = R.color.text_tertiary),
                fontWeight = FontWeight.Medium,
            )

        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
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
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) { page ->
                    val shot = shots.value[page]

                    CommonCard(
                        title = shot.title,
                        tag = shot.status,
                        content = shot.prompt,
                        imageUrl = getShotImage(shot),
                        backgroundColor = colorResource(id = R.color.card_background),
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
            enabled = shots.value.isNotEmpty() && !isLoadingVideo,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (!allCompleted) {
                    ToastUtils.showShort(context, "Please wait for all shots to be completed")
                    return@CommonButton
                }

                // 全部生成完成，生成视频（POST /api/story/{id}/generate-video）
                storyViewModel.generateVideo(storyId)
            }
        )
    }
    // 生成进度遮罩
    CommonLoadingOverlay(
        loading = isLoadingVideo,
        type = LoadingType.GENERATING,
    )
}

@Composable
fun getShotImage(shot: ShotUI): Any {
    val status = Status.from(shot.status)
    return when (status) {
        Status.COMPLETED -> shot.imageUrl ?: R.drawable.placeholder_completed
        Status.GENERATING -> R.drawable.placeholder_generating
        Status.FAILED -> R.drawable.placeholder_failed
    }
}
