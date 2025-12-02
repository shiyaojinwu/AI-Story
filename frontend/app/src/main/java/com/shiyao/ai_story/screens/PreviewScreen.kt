package com.shiyao.ai_story.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonVideoPlayer
import com.shiyao.ai_story.utils.StorageUtils.saveNetworkVideoToMediaStore
import com.shiyao.ai_story.utils.ToastUtils
import kotlinx.coroutines.launch

@Composable
fun PreviewScreen(
    navController: NavController,
    url: String,
    title: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videoUri = url.toUri()

    var isDownloading by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "←",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary)
                )
                Text(
                    " Back",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary)
                )
            }

            Text(
                text = "Preview",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 0.dp),
                color = colorResource(id = R.color.text_secondary)
            )

            CommonVideoPlayer(
                uri = videoUri,
                autoPlay = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .clip(RoundedCornerShape(16.dp)),
                height = 280.dp
            )

            Text(
                text = "Story: $title",
                color = colorResource(id = R.color.text_secondary),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            )

            // 📥 导出按钮
            CommonButton(
                text = "Export Video",
                backgroundColor = colorResource(id = R.color.primary),
                contentColor = Color.White,
                fontSize = 20,
                horizontalPadding = 14,
                verticalPadding = 18,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
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
        // ⚠下载进度遮罩
        if (isDownloading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}, // 阻止点击
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = colorResource(id = R.color.primary))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("下载中... $progress%", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}