package com.shiyao.ai_story.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shiyao.ai_story.TraditionalActivity

/**
 * 首页屏幕
 * @param navController 导航控制器
 */
@Composable
fun CreateScreen(navController: NavController) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "首页 (Compose)")

        Button(
            onClick = { navController.navigate("Assets") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "跳转到详情页 (Compose)")
        }

        Button(
            onClick = {
                // 跳转到传统的 XML Activity
                val intent = Intent(context, TraditionalActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "跳转到传统 XML 页面")
        }
    }
}
