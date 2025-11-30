package com.shiyao.ai_story.screens

import android.R.attr.progress
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonVideoPlayer
import com.shiyao.ai_story.viewmodel.AssetsViewModel


@Composable
fun PreviewScreen(navController: NavController, assetsViewModel: AssetsViewModel) {
    val context = LocalContext.current
    val selectedAsset by assetsViewModel.selectedAsset.collectAsState()
    val realVideoUrl = selectedAsset?.videoUrl ?: ""
    val realTitle = selectedAsset?.title ?: "Unknown Story"
    val exportState by assetsViewModel.exportState.collectAsState()
    val progress by assetsViewModel.progressPercentage.collectAsState()

    val fallbackUrl = "https://v-cdn.zjol.com.cn/280443.mp4"

    val urlToPlay = if (realVideoUrl.isNotEmpty()) realVideoUrl else fallbackUrl
    LaunchedEffect(exportState) {
        when (exportState) {
            1 -> Toast.makeText(context, "开始下载...", Toast.LENGTH_SHORT).show()
            2 -> {
                Toast.makeText(context, "导出成功！已保存到相册", Toast.LENGTH_LONG).show()
                assetsViewModel.resetExportState()
            }
            -1 -> {
                Toast.makeText(context, "导出失败，请检查网络", Toast.LENGTH_SHORT).show()
                assetsViewModel.resetExportState()
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
                .clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.primary))
            Text(" Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.primary))
        }

        Text(
            text = "Preview",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 0.dp),
            color = colorResource(id = R.color.text_secondary)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            // 接入播放器
            CommonVideoPlayer(
                uri = urlToPlay.toUri(),
                autoPlay = true,
                height = 240.dp
            )
        }

        Text(
            text = "Story: $realTitle",
            color = colorResource(id = R.color.text_secondary),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

// 5. 导出按钮
        Button(
            onClick = {
                assetsViewModel.exportCurrentVideo(context)
            },
            enabled = exportState != 1,

            shape = RoundedCornerShape(50),


            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.primary), // 蓝底
                contentColor = Color.White,
                disabledContainerColor = Color.Gray // 禁用时变灰
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if (exportState == 1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 环形进度条 (progress 参数 0.0 ~ 1.0)
                    CircularProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Downloading... $progress%", fontSize = 16.sp)
                }
            } else {
                // 正常状态
                Text("Export Video", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

