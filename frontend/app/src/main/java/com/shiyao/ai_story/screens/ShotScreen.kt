package com.shiyao.ai_story.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import com.shiyao.ai_story.components.BottomNavBar
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
    storyId: String,
    shotViewModel: ShotViewModel,
    storyViewModel: StoryViewModel
) {
    // 分镜列表
    val shots = shotViewModel.shots.collectAsState()
    // 所有分镜都完成
    val allCompleted = shots.value.all { it.status == Status.COMPLETED.value }
    // 故事标题
    val storyTitle = shotViewModel.storyTitle.collectAsState()
    // 所有分镜都完成或失败
    val allShotsCompletedOrFail = shotViewModel.allShotsCompletedOrFail.collectAsState()
    // 生成视频状态
    val generateVideoState by storyViewModel.generateVideoState.collectAsState()
    val isLoadingVideo = generateVideoState is UIState.Loading
    // 上下文
    val context = LocalContext.current

    // 翻页器
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { shots.value.size }
    )

    // 监听生成视频状态：成功后再跳转预览页；失败时给出提示
    LaunchedEffect(generateVideoState) {
        if (generateVideoState.isSuccess) {
            val videoUrl = generateVideoState.getOrNull()?.videoUrl
            val title = generateVideoState.getOrNull()?.title?: "completed"
            if (videoUrl.isNullOrEmpty()) {
                ToastUtils.showLong(context, "生成视频失败")
            } else {
                // 生成成功才跳转预览页
                navController.navigate(AppRoute.previewRoute(videoUrl, title)) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
            storyViewModel.clearGenerateVideoState()
        } else if (generateVideoState.isError) {
            val message = (generateVideoState as UIState.Error).message ?: "生成视频失败"
            ToastUtils.showLong(context, message)
            storyViewModel.clearGenerateVideoState()
        }
    }

    DisposableEffect(Unit) {
        // 轮询分镜
        shotViewModel.pollShotsUntilCompleted(storyId)

        onDispose {
            shotViewModel.stopPolling()
            shotViewModel.refreshShots()
            shotViewModel.clearStoryTitle()
        }
    }

    Scaffold(
        containerColor = colorResource(id = R.color.background),
        topBar = { TopBackBar { navController.popBackStack() } },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 副标题和故事标题
            Text(
                text = "Storyboard",
                color = colorResource(id = R.color.text_secondary),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = storyTitle.value,
                fontSize = 30.sp,
                lineHeight = 35.sp,
                color = colorResource(id = R.color.text_tertiary),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 18.dp)
            )

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
                    if (shots.value.isEmpty()) {
                        // 空状态
                        CommonCard(
                            tag = "please wait for generating",
                            backgroundColor = colorResource(id = R.color.card_background),
                            imageHeight = 230.dp
                        )
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentPadding = PaddingValues(end = 90.dp),
                            pageSpacing = 16.dp
                        ) { page ->
                            val shot = shots.value[page]
                            CommonCard(
                                title = shot.title,
                                tag = shot.status,
                                content = shot.prompt,
                                imageUrl = getShotUIImage(shot),
                                backgroundColor = colorResource(id = R.color.card_background),
                                imageHeight = 200.dp,
                                modifier = Modifier.clickable {
                                    if (allShotsCompletedOrFail.value) {
                                        navController.navigate(AppRoute.shotDetailRoute(shot.id))
                                    } else {
                                        //navController.navigate(AppRoute.shotDetailRoute(shot.id))
                                        ToastUtils.showLong(context, "请等待所有分镜生成完成")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 生成视频按钮
            CommonButton(
                text = "Generate Video",
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = Color.White,
                fontSize = 30,
                horizontalPadding = 16,
                verticalPadding = 16,
                enabled = shots.value.isNotEmpty() && !isLoadingVideo,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (!allCompleted) {
                        ToastUtils.showShort(context, "Please wait for all shots to be completed")
                        return@CommonButton
                    }
                    storyViewModel.generateVideo(storyId)
                }
            )
        }

        // 加载遮罩
        CommonLoadingOverlay(
            loading = isLoadingVideo,
            type = LoadingType.GENERATING
        )
    }
}

@Composable
fun getShotUIImage(shot: ShotUI): Any? {
    val status = Status.from(shot.status)
    return when (status) {
        Status.COMPLETED -> shot.imageUrl?:R.drawable.placeholder_default
        Status.GENERATING -> null
        Status.FAILED -> R.drawable.placeholder_failed
    }
}
