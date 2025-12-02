package com.shiyao.ai_story.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiyao.ai_story.R


/**
 * 通用加载遮罩组件（生成中/下载中）
 * @param modifier           外部传入的 Modifier
 * @param loading            是否显示加载遮罩
 * @param type               加载类型（生成中 / 下载中）
 * @param progress           下载进度，仅在 type = DOWNLOADING 时使用
 * @param onDismiss          点击遮罩是否关闭，null 表示禁止点击
 */
@Composable
fun CommonLoadingOverlay(
    modifier: Modifier = Modifier,
    loading: Boolean,
    type: LoadingType,   // 下载、生成
    progress: Int = 0,   // 下载才用
    onDismiss: (() -> Unit)? = null
) {
    if (!loading) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .let {
                if (onDismiss == null) it.clickable(enabled = false) {}
                else it.clickable { onDismiss() }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = colorResource(id = R.color.primary))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (type) {
                    LoadingType.GENERATING -> "生成中..."
                    LoadingType.DOWNLOADING -> "下载中... $progress%"
                },
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

enum class LoadingType {
    GENERATING,
    DOWNLOADING
}
