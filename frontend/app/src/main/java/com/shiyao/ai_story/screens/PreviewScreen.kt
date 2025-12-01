package com.shiyao.ai_story.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold // ⚠️ 导入 Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.viewmodel.ShotViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 模拟的导出功能 (保持不变)
suspend fun exportVideoToGallery(context: Context, videoUri: Uri) {
    withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) { Toast.makeText(context, "开始导出视频...", Toast.LENGTH_SHORT).show() }
        kotlinx.coroutines.delay(1500) // 模拟导出耗时
        withContext(Dispatchers.Main) { Toast.makeText(context, "视频已导出到相册!", Toast.LENGTH_LONG).show() }
    }
}

@Composable
fun PreviewScreen(
    navController: NavController,
    assetName: String,
    shotViewModel: ShotViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val videoPath by shotViewModel.previewVideoPath.collectAsState()

    val defaultMockVideoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4"
    val currentVideoPath = videoPath ?: defaultMockVideoUrl
    val videoUri = remember(currentVideoPath) { Uri.parse(currentVideoPath) }

    // ⚠️ 步骤 1: 使用 Scaffold 并设置白色背景
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // ⚠️ 步骤 2: 应用 Scaffold 的内边距
                .padding(horizontal = 20.dp) // ⚠️ 步骤 2: 应用统一的水平内边距
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp) // 保持顶部间距
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.primary))
                Text(" Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.primary))
            }

            Text(
                text = "Preview",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 0.dp), // 移除 start padding
                color = colorResource(id = R.color.text_secondary)
            )

            // ⚠️ 步骤 3: 调整视频播放器尺寸和间距
            VideoPlayer(
                videoUri = videoUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f) // 使用宽高比替代固定高度
                    .padding(top = 20.dp) // 与标题的间距
            )

            Text(
                text = "Story: $assetName",
                color = colorResource(id = R.color.text_secondary),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp) // ⚠️ 步骤 3: 与播放器的间距
            )

            Spacer(modifier = Modifier.weight(1f))

            // ⚠️ 步骤 5: 添加辅助文本
            Text(
                text = "The storyboard will open automatically after genvien.", // 来自桌面版图片
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
            )

            // ⚠️ 步骤 4: 调整按钮 Modifier
            CommonButton(
                text = "Export Video",
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = Color.White,
                fontSize = 20,
                horizontalPadding = 14,
                verticalPadding = 18,
                modifier = Modifier
                    .fillMaxWidth() // 按钮将填满 20dp 内边距内的宽度
                    .padding(bottom = 40.dp), // 仅保留底部间距
                onClick = {
                    scope.launch {
                        exportVideoToGallery(context, videoUri)
                    }
                }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUri: Uri, modifier: Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                Lifecycle.Event.ON_DESTROY -> exoPlayer.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = true
                setShowNextButton(false)
                setShowPreviousButton(false)
            }
        },
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)) // 保持圆角
            .background(Color.Black)
    )
}