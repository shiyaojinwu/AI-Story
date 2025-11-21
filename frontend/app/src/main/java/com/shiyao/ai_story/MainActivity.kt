package com.shiyao.ai_story

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.shiyao.ai_story.navigation.AppNavigation
import com.shiyao.ai_story.ui.theme.AIStoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 使用应用主题
            AIStoryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // 初始化导航控制器
                    val navController = rememberNavController()
                    // 设置导航图
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}
