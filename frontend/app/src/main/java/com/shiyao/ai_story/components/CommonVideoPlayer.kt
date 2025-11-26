package com.shiyao.ai_story.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.shiyao.ai_story.utils.ExoPlayerHelper

/**
 * 通用视频播放器 Compose 组件
 * @param modifier 布局修饰符
 * @param uri 视频资源 Uri（可本地或网络）
 * @param autoPlay 是否自动播放
 * @param height 播放器高度
 */
@OptIn(UnstableApi::class)
@Composable
fun CommonVideoPlayer(
    modifier: Modifier = Modifier,
    uri: Uri,
    autoPlay: Boolean = false,
    height: Dp = 240.dp
) {
    val context = LocalContext.current
    val player = ExoPlayerHelper.getPlayer(context)

    // 当 uri 改变时重新加载媒体资源
    LaunchedEffect(uri) {
        ExoPlayerHelper.preparePlayer(context, uri, autoPlay)
    }
    // 清理
    DisposableEffect(Unit) {
        onDispose {
            ExoPlayerHelper.releasePlayer()
        }
    }
    AndroidView(
        factory = {
            PlayerView(context).apply {
                this.player = player
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}


@Composable
@Preview
fun VideoScreen() {
    val videoUrl =
        "http://flv4mp4.people.com.cn/videofile7/pvmsvideo/2023/4/14/DangWang-BoChenDi_7a0283ace3c035c20500c33dfaef44ed.mp4"
    CommonVideoPlayer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        uri = videoUrl.toUri(),
        autoPlay = false,
        height = 250.dp
    )
}
