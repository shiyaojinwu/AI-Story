package com.shiyao.ai_story.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.BottomNavBar
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonLoadingOverlay
import com.shiyao.ai_story.components.CommonVideoPlayer
import com.shiyao.ai_story.components.LoadingType
import com.shiyao.ai_story.components.TopBackBar
import com.shiyao.ai_story.utils.StorageUtils.saveNetworkVideoToMediaStore
import com.shiyao.ai_story.utils.ToastUtils
import kotlinx.coroutines.launch

@Composable
fun PreviewScreen(
    navController: NavController,
    url: String,
    title: String,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videoUri = url.toUri()

    var isDownloading by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = colorResource(id = R.color.background),
        bottomBar = {
            BottomNavBar(
                navController = navController
            )

        },
        topBar = {
            TopBackBar(
                content = "Video",
                onBack = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Preview",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp),
                color = colorResource(id = R.color.text_secondary)
            )

            CommonVideoPlayer(
                uri = videoUri,
                autoPlay = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp)
                    .clip(RoundedCornerShape(16.dp)),
                height = 280.dp
            )

            Text(
                text = "Story: $title",
                color = colorResource(id = R.color.text_secondary),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 28.dp)
            )

            // 导出按钮
            CommonButton(
                text = "Export Video",
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = Color.White,
                fontSize = 20,
                horizontalPadding = 14,
                verticalPadding = 18,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp),
                onClick = {
                    scope.launch {
                        isDownloading = true
                        progress = 0

                        val result = saveNetworkVideoToMediaStore(
                            context = context,
                            url = url,
                            fileName = "${title}.mp4",
                            onProgress = { p ->
                                progress = p
                            }
                        )

                        isDownloading = false

                        if (result != null) {
                            ToastUtils.showShort(context, "视频已保存到相册！")
                        } else {
                            ToastUtils.showShort(context, "保存失败")
                        }
                    }
                }
            )

        }
    }
    // 下载进度遮罩
    CommonLoadingOverlay(
        loading = isDownloading,
        type = LoadingType.DOWNLOADING,
        progress = progress
    )
}