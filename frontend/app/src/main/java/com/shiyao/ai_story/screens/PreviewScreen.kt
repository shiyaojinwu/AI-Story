package com.shiyao.ai_story.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    val fallbackUrl = "http://flv4mp4.people.com.cn/videofile7/pvmsvideo/2023/4/14/DangWang-BoChenDi_7a0283ace3c035c20500c33dfaef44ed.mp4"

    val urlToPlay = if (realVideoUrl.isNotEmpty()) realVideoUrl else fallbackUrl


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

        CommonButton(
            text = "Export Video",
            backgroundColor = colorResource(id = R.color.primary),
            contentColor = Color.White,
            fontSize = 20,
            horizontalPadding = 14,
            verticalPadding = 18,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
            onClick = { Toast.makeText(context, "Exporting...", Toast.LENGTH_SHORT).show() }
        )
    }
}

