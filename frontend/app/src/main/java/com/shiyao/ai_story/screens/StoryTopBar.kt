package com.shiyao.ai_story.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiyao.ai_story.R

/**
 * 顶部通用栏：
 * - 在需要返回的页面显示 Back 按钮
 * - 在 CreateScreen 等首页不显示 Back，但保留同样的高度，保证下方标题对齐
 * TODO 完善成顶部导航栏
 */

//文本字号
val StoryTitleFontSize = 28.sp
val StoryBackFontSize = 18.sp

@Composable
fun StoryTopBar(
    showBack: Boolean,
    showTitle: Boolean = true,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (showBack && onBack != null) {
            Text(
                text = "< Back",
                fontSize = StoryBackFontSize,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.primary),
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        if (showTitle) {
            // 标题
            Text(
                text = "StoryFlow",
                fontSize = StoryTitleFontSize,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.text_tertiary),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


