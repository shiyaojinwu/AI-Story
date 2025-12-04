package com.shiyao.ai_story.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.BottomNavBar
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonCard
import com.shiyao.ai_story.components.CommonLoadingOverlay
import com.shiyao.ai_story.components.CommonTextField
import com.shiyao.ai_story.components.LoadingType
import com.shiyao.ai_story.components.TopBackBar
import com.shiyao.ai_story.model.enums.Status
import com.shiyao.ai_story.model.response.ShotDetailResponse
import com.shiyao.ai_story.viewmodel.ShotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotDetailScreen(
    shotId: String,
    navController: NavController,
    shotViewModel: ShotViewModel
) {
    val shot by shotViewModel.currentEditingShot.collectAsState()
    val generateState by shotViewModel.generateShotState.collectAsState()
    val isLoading = generateState.isLoading

    var transitionExpanded by remember { mutableStateOf(false) }
    val transitionOptions = listOf("Ken Burns", "Crossfade", "Volume Mix")

    // 拉取详情
    DisposableEffect(shotId) {
        shotViewModel.getShotDetail(shotId)
        onDispose {
            shotViewModel.refreshCurrentEditingShot()
            shotViewModel.clearGenerateState()
        }
    }

    // 加载页
    if (shot == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        containerColor = colorResource(id = R.color.background),
        topBar = { TopBackBar { navController.popBackStack() } },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
        ) {
            // 滚动内容
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Shot Detail",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_secondary),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CommonCard(
                    tag = shot!!.status,
                    imageUrl = getShotImage(shot!!)
                )

                // Shot Description
                SectionTitle("Shot description")
                CommonTextField(
                    placeholder = shot!!.prompt,
                    value = shot!!.prompt,
                    onValueChange = shotViewModel::updateShotDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 55.dp)
                        .padding(vertical = 8.dp),
                )


                // Transition
                SectionTitle("Video Transition")
                ExposedDropdownMenuBox(
                    expanded = transitionExpanded,
                    onExpandedChange = { transitionExpanded = !transitionExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = shot!!.transition,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = transitionExpanded)
                        },
                        placeholder = { Text("Ken Burns Effect") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            ),
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
                                }
                            )
                        }
                    }
                }

                // Narration
                SectionTitle("Narration text")
                CommonTextField(
                    placeholder = shot!!.narration,
                    value = shot!!.narration,
                    onValueChange = shotViewModel::updateShotNarration,
                    modifier = Modifier.padding(vertical = 8.dp)
                        .heightIn(min = 55.dp)
                        .padding(vertical = 8.dp),
                )
            }

            // 底部按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CommonButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp, start = 8.dp),
                    text = "Generate Image",
                    backgroundColor = colorResource(id = R.color.primary),
                    contentColor = colorResource(id = R.color.text),
                    fontSize = 22,
                    enabled = !isLoading,
                    onClick = { if (!isLoading) shotViewModel.updateAndGenerateShot() }
                )
            }
        }
    }

    CommonLoadingOverlay(
        loading = isLoading,
        type = LoadingType.GENERATING,
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun getShotImage(shot: ShotDetailResponse): Any? {
    val status = Status.from(shot.status)
    return when (status) {
        Status.COMPLETED -> shot.imageUrl ?: R.drawable.placeholder_default
        Status.GENERATING -> null
        Status.FAILED -> R.drawable.placeholder_failed
    }
}
