package com.shiyao.ai_story.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiyao.ai_story.R

/**
 * 通用文本输入框组件
 * @param placeholder 文本框为空时显示的占位符
 * @param value 当前文本内容
 * @param onValueChange 文本内容变化回调
 * @param modifier Modifier 修饰符，可用于设置额外布局属性
 * @param label 文本框标签（可选），为空则不显示
 * @param enabled 是否可用，false 时文本框不可编辑
 * @param readOnly 是否只读，true 时无法修改内容
 * @param width 文本框宽度，默认填满父布局（Dp.Unspecified 表示不指定宽度）
 * @param height 文本框高度，默认自适应内容（Dp.Unspecified 表示不指定高度）
 * @param backgroundColor 背景颜色，默认使用资源颜色 R.color.edit_background
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTextField(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    backgroundColor: Color = colorResource(id = R.color.edit_background),
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .then(
                if (width != Dp.Unspecified) Modifier.width(width)
                else Modifier.fillMaxWidth()
            )
            .then(
                if (height != Dp.Unspecified) Modifier.height(height)
                else Modifier
            ),
        label = if (label.isNotEmpty()) {
            { Text(text = label) }
        } else null,
        placeholder = { Text(text = placeholder, color = colorResource(id = R.color.text_hint)) },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = TextStyle(fontSize = 16.sp),
        shape = RoundedCornerShape(8.dp),
        maxLines = Int.MAX_VALUE,

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(id = R.color.divider),
            unfocusedBorderColor = colorResource(id = R.color.divider_unfocused),
            disabledBorderColor = colorResource(id = R.color.divider_unfocused).copy(alpha = 0.5f),

            focusedPlaceholderColor = colorResource(id = R.color.divider).copy(alpha = 0.7f),
            unfocusedPlaceholderColor = colorResource(id = R.color.divider).copy(alpha = 0.7f),

            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            errorContainerColor = backgroundColor
        )
    )
}


/**
 * 通用文本输入框组件 (内部管理状态)
 * @param placeholder 占位符文字
 * @param onValueChange 值变化事件
 * @param modifier Modifier
 * @param label 标签文字
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTextFieldWithState(
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    backgroundColor: Color = colorResource(id = R.color.edit_background)
) {
    var textValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    CommonTextField(
        placeholder = placeholder,
        value = textValue.text,
        onValueChange = { newValue ->
            textValue = TextFieldValue(newValue)// 更新状态 触发重组
            onValueChange(newValue)// 将值传递给外部回调
        },
        modifier = modifier,
        label = label,
        width = width,
        height = height,
        backgroundColor = backgroundColor
    )
}

@Preview
@Composable
fun CommonTextFieldPreview() {
    CommonTextField(
        placeholder = "write_your_story",
        value = "",
        onValueChange = { Log.d("Story-outStatus", it) },
        label = "这是外部管理状态的输入框，预览不会保存输入内容"
    )
}

@Preview
@Composable
fun CommonTextFieldWithStatePreview() {
    CommonTextFieldWithState(
        placeholder = "write_your_story",
        onValueChange = { Log.d("Story-innerStatus", it) },
        label = "这是内部管理状态的输入框，预览会保存输入内容",
        width = 300.dp,
        height = 100.dp
    )
}