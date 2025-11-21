package com.shiyao.ai_story.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiyao.ai_story.R

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
