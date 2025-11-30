package com.shiyao.ai_story.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonCard
import com.shiyao.ai_story.model.enums.ShotStatus
import com.shiyao.ai_story.viewmodel.ShotViewModel
import com.shiyao.ai_story.viewmodel.StoryViewModel

/**
 * 分镜生成与视频创建屏幕
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenerateStoryScreen(
    navController: NavController,
    storyId: String,
    shotViewModel: ShotViewModel,
    storyViewModel: StoryViewModel
) {
    val shots = shotViewModel.shots.collectAsState()
    val storyTitle = storyViewModel.storyTitle.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { shots.value.size }
    )

    DisposableEffect(Unit) {
        val title = storyViewModel.storyTitle.value
        shotViewModel.pollShotsUntilCompleted(storyId, title)

        onDispose {
            shotViewModel.stopPolling()   // 停止轮询
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "← Back",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "Storyboard",
                fontSize = 28.sp
            )

            Text(
                text = storyTitle.value,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
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
                            .height(400.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        pageSpacing = 16.dp
                    ) { page ->
                        val shot = shots.value[page]
                        val status = ShotStatus.from(shot.status)

                        when (status) {
                            ShotStatus.COMPLETED -> {
                                shot.imageUrl?.let {
                                    CommonCard(
                                        title = shot.title,
                                        tag = "Generated",
                                        imageUrl = it,
                                        backgroundColor = colorResource(id = R.color.card_background),
                                    )
                                }
                            }

                            ShotStatus.GENERATING, ShotStatus.FAILED -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Loading shots...",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(pagerState.pageCount) { index ->
                            val color =
                                if (pagerState.currentPage == index) Color(0xFFFFFFFF)
                                else Color.Gray.copy(alpha = 0.5f)

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // TODO: 创建视频
//                shotViewModel.generateVideo(storyId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Generate Video",
                fontSize = 18.sp
            )
        }
    }
}
