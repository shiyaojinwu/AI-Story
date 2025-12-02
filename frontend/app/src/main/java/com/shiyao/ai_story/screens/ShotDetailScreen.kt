package com.shiyao.ai_story.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonTextField
import com.shiyao.ai_story.model.enums.Status
import com.shiyao.ai_story.viewmodel.ShotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotDetailScreen(
    shotId: String?,
    navController: NavController,
    shotViewModel: ShotViewModel
) {
    val shot by shotViewModel.currentEditingShot.collectAsState()
    val generateState by shotViewModel.generateShotState.collectAsState()
    val isLoading = generateState.isLoading

    if (shotId == null) {
        navController.popBackStack()
        return
    }
    DisposableEffect(shotId) {
        shotViewModel.getShotDetail(shotId)
        onDispose {
            shotViewModel.refreshCurrentEditingShot()
            shotViewModel.clearGenerateState()
        }
    }
    // 模拟下拉菜单状态
    var transitionExpanded by remember { mutableStateOf(false) }
    val transitionOptions = listOf("Ken Burns Effect", "Fade In/Out", "Slide Transition")

    // 避免空数据下使用 shot!! 闪退
    if (shot == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.primary),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading shot...",
                    color = colorResource(id = R.color.text_secondary),
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    Scaffold(
        containerColor = colorResource(id = R.color.background)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // 顶部导航和标题
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← Back",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.text_secondary),
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Shot Detail",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_secondary)
                )
            }

            // 1. 视频/图像预览区域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(contentAlignment = Alignment.Center) {

                    when (shot?.status) {

                        Status.GENERATING.value -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(8.dp))
                                Text("Generating...", color = Color.Gray)
                            }
                        }

                        Status.COMPLETED.value -> {
                            AsyncImage(
                                model = shot?.imageUrl,
                                contentDescription = "Shot Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Status.FAILED.value -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Generate Failed", color = Color.Red)
                            }
                        }

                        else -> {
                            Text("No Shot Preview", color = Color.LightGray)
                        }
                    }

                    if (shot?.status == Status.COMPLETED.value) {
                        Text(
                            text = "Generated",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .background(Color(0xFF4BB543), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // 2. 镜头描述 (Shot Description)
            Text(
                "Shot description",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            CommonTextField(
                placeholder = "A misty forest at dawn with a tent",
                value = shot?.prompt ?: "",
                onValueChange = shotViewModel::updateShotDescription,
                height = 55.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 3. 视频转场 (Video Transition) - 使用 ExposedDropdownMenuBox 替代 CommonTextField 的 trailingIcon
            Text(
                "Video Transition",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = transitionExpanded,
                onExpandedChange = { transitionExpanded = !transitionExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // OutlinedTextField 作为锚点，使用 CommonTextField 的样式
                OutlinedTextField(
                    value = shot?.transition ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transitionExpanded) },
                    placeholder = { Text("Ken Burns Effect") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.edit_background),
                        unfocusedContainerColor = colorResource(id = R.color.edit_background),
                        focusedBorderColor = colorResource(id = R.color.primary),
                        unfocusedBorderColor = colorResource(id = R.color.edit_background)
                    )
                )

                ExposedDropdownMenu(
                    expanded = transitionExpanded,
                    onDismissRequest = { transitionExpanded = false }
                ) {
                    transitionOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                shotViewModel.updateShotTransition(option)
                                transitionExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // 4. 旁白文本 (Narration text)
            Text(
                "Narration text",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
            CommonTextField(
                placeholder = "Here is the narration text...",
                value = shot?.narration ?: "",
                onValueChange = shotViewModel::updateShotNarration,
                height = 100.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 5. 底部按钮区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 生成按钮
                CommonButton(
                    text = "Generate Image",
                    backgroundColor = colorResource(id = R.color.edit_background),
                    contentColor = colorResource(id = R.color.text),
                    fontSize = 16,
                    horizontalPadding = 16,
                    verticalPadding = 12,
                    enabled = !isLoading,
                    onClick = {
                        if (!isLoading) {
                            shotViewModel.updateAndGenerateShot()
                        }
                    }
                )
            }
            // Loading 弹窗
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.primary),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.padding(top = 16.dp))
                        Text(
                            text = "重新生成中...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}