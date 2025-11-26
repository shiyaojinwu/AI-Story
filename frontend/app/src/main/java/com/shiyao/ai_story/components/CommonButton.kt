package com.shiyao.ai_story.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiyao.ai_story.R
import com.shiyao.ai_story.utils.StorageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * 通用按钮组件
 * @param text 按钮文字
 * @param onClick 点击事件
 * @param modifier Modifier
 * @param enabled 是否可用
 * @param backgroundColor 背景颜色
 * @param contentColor 文字颜色
 */
@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = colorResource(id = R.color.edit_background),
    contentColor: Color = colorResource(id = R.color.text),
    fontSize: Int = 18,
    horizontalPadding: Int = 16,
    verticalPadding: Int = 8
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(
            horizontal = horizontalPadding.dp,
            vertical = verticalPadding.dp
        ),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun CommonButtonPreview() {
    CommonButton(text = "Button", onClick = {})
}

@Composable
@Preview
fun FileDemoScreen() {
    val context = LocalContext.current

    var localVideoUri by remember { mutableStateOf<Uri?>(null) }
    var localVideoSize by remember { mutableStateOf<Long?>(null) }

    var networkVideoUri by remember { mutableStateOf<Uri?>(null) }
    var networkVideoSize by remember { mutableStateOf<Long?>(null) }
    var downloadStatus by remember { mutableStateOf("未下载") }

    var internalFilePath by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("文件存储")

        // 保存视频到 MediaStore
        CommonButton(
            text = "保存视频到 MediaStore",
            onClick = {
                val dummyVideo: InputStream = ByteArrayInputStream(ByteArray(1024 * 1024)) // 1MB 占位
                val fileName = "demo_video.mp4"
                val uri = StorageUtils.saveVideoToMediaStore(context, dummyVideo, fileName)
                localVideoUri = uri
                localVideoSize = uri?.let { StorageUtils.getFileSizeFromUri(context, it) }
            },
            modifier = Modifier,
            enabled = true,
            backgroundColor = colorResource(id = R.color.status_bar),
        )

        localVideoUri?.let {
            Text("已保存到 MediaStore: $it")
            Text("文件大小: $localVideoSize 字节")
        }

        // 保存网络视频到 MediaStore
        CommonButton(
            text = "保存网络视频到 MediaStore",
            onClick = {
                val videoUrl = "https://cdn.creazilla.com/videos/15538396/black-bats-video--md.mp4"
                downloadStatus = "下载中..."
                CoroutineScope(Dispatchers.Main).launch {
                    val uri = StorageUtils.saveNetworkVideoToMediaStore(
                        context,
                        url = videoUrl,
                        fileName = "network_video.mp4"
                    )
                    if (uri != null) {
                        networkVideoUri = uri
                        networkVideoSize = StorageUtils.getFileSizeFromUri(context, uri)
                        downloadStatus = "下载成功 ✅"
                    } else {
                        downloadStatus = "下载失败 ❌"
                    }
                    networkVideoUri = uri
                    networkVideoSize = uri?.let { StorageUtils.getFileSizeFromUri(context, it) }
                }
            },
            modifier = Modifier,
            enabled = true,
            backgroundColor = colorResource(id = R.color.status_bar),
        )
        Text("状态: $downloadStatus")
        networkVideoUri?.let {
            Text("已保存到 MediaStore: $it")
            Text("文件大小: $networkVideoSize 字节")
        }

        // 保存文件到内部存储
        CommonButton(
            text = "保存文件到内部存储",
            onClick = {
                val dummyFile: InputStream =
                    ByteArrayInputStream("Hello Compose Storage!".toByteArray())
                val fileName = "demo_file.txt"
                val savedFile = StorageUtils.saveFileToInternalStorage(context, dummyFile, fileName)
                internalFilePath = savedFile?.absolutePath
            },
            modifier = Modifier,
            enabled = true,
            backgroundColor = colorResource(id = R.color.status_bar),
        )

        internalFilePath?.let { path ->
            Text("内部文件路径: $path")
        }
    }
}
