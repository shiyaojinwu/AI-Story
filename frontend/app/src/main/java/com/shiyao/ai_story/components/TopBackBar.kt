package com.shiyao.ai_story.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
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
 * 通用顶部返回栏，可嵌入 Scaffold 的 topBar
 * @param title         标题
 * @param onBack        返回事件（默认 popBackStack）
 */
@Composable
fun TopBackBar(
    title: String = "Back",
    content: String? = null,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        onBack?.let {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .clickable { onBack.invoke() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回箭头图标
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(id = R.color.primary),
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 标题文字
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.primary)
                )
            }
        }

        content?.let {
            Text(
                text = it,
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.text_tertiary)
            )
        }
    }
}