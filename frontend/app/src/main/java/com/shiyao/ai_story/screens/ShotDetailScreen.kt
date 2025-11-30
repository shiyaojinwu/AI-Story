package com.shiyao.ai_story.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.shiyao.ai_story.viewmodel.ShotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotDetailScreen(
    navController: NavController,
    shotViewModel: ShotViewModel
) {
    val shotState by shotViewModel.currentEditingShot.collectAsState()
    val shot = shotState

    if (shot == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    // 模拟下拉菜单状态
    var transitionExpanded by remember { mutableStateOf(false) }
    val transitionOptions = listOf("Ken Burns Effect", "Fade In/Out", "Slide Transition")

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
                    AsyncImage(
                        model = shot.imageUrl,
                        contentDescription = "Shot Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

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

            // 2. 镜头描述 (Shot Description)
            Text("Shot description", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            CommonTextField(
                placeholder = "A misty forest at dawn with a tent",
                value = shot.description,
                onValueChange = shotViewModel::updateShotDescription,
                height = 55.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 3. 视频转场 (Video Transition) - 使用 ExposedDropdownMenuBox 替代 CommonTextField 的 trailingIcon
            Text("Video Transition", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))

            ExposedDropdownMenuBox(
                expanded = transitionExpanded,
                onExpandedChange = { transitionExpanded = !transitionExpanded },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                // OutlinedTextField 作为锚点，使用 CommonTextField 的样式
                OutlinedTextField(
                    value = shot.transition,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transitionExpanded) },
                    placeholder = { Text("Ken Burns Effect") },
                    modifier = Modifier.menuAnchor().fillMaxWidth().height(55.dp),
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
            Text("Narration text", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            CommonTextField(
                placeholder = "Here is the narration text...",
                value = shot.narration,
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
                // 确认修改按钮
                CommonButton(
                    text = "Confirm Modification",
                    backgroundColor = colorResource(id = R.color.edit_background),
                    contentColor = colorResource(id = R.color.text),
                    fontSize = 16,
                    horizontalPadding = 16,
                    verticalPadding = 12,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shotViewModel.saveShotChanges()
                    }
                )

                // 修改完成按钮 (跳转回 GenerateStoryScreen)
                CommonButton(
                    text = "Modification Done",
                    backgroundColor = colorResource(id = R.color.primary),
                    contentColor = Color.White,
                    fontSize = 16,
                    horizontalPadding = 16,
                    verticalPadding = 12,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        shotViewModel.saveShotChanges()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}