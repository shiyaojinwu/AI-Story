package com.shiyao.ai_story.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.BottomNavBar
import com.shiyao.ai_story.components.CommonButton
import com.shiyao.ai_story.components.CommonTextField
import com.shiyao.ai_story.components.TopBackBar
import com.shiyao.ai_story.model.enums.Style
import com.shiyao.ai_story.navigation.AppRoute
import com.shiyao.ai_story.utils.ToastUtils
import com.shiyao.ai_story.viewmodel.StoryViewModel
import com.shiyao.ai_story.viewmodel.UIState

/**
 * 首页屏幕
 * @param navController 导航控制器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    navController: NavController,
    storyViewModel: StoryViewModel,
) {
    val selectedStyle by storyViewModel.selectedStyle.collectAsState()
    val storyContent by storyViewModel.storyContent.collectAsState()
    val generateState by storyViewModel.generateStoryState.collectAsState()

    val context = LocalContext.current
    val isLoading = generateState is UIState.Loading

    //todo 限制生成次数
    LaunchedEffect(generateState) {
        if (generateState.isSuccess) {
            val storyId = generateState.getOrNull()
            if (storyId != null) {
                navController.navigate(AppRoute.generateShotRoute(storyId))
                storyViewModel.clearGenerateState() // 清空状态
            }
        }
        if (generateState.isError) {
            val message = (generateState as UIState.Error).message ?: "生成失败"
            ToastUtils.showLong(context, message)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.background))
                    .padding(22.dp)
                    .padding(paddingValues)
            ) {
                // 顶部栏
                TopBackBar(content = "StoryFlow")

                Spacer(modifier = Modifier.padding(bottom = 16.dp))


                Text(
                    text = stringResource(id = R.string.create),
                    color = colorResource(id = R.color.text_secondary),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 18.dp)
                )

                CommonTextField(
                    placeholder = stringResource(id = R.string.write_your_story),
                    value = storyContent,
                    onValueChange = { storyViewModel.setStoryContent(it) },
                    height = 168.dp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StyleButton(
                        title = stringResource(id = R.string.movie),
                        isSelected = selectedStyle == Style.MOVIE
                    ) { storyViewModel.setStyle(Style.MOVIE) }

                    Spacer(Modifier.width(8.dp))

                    StyleButton(
                        title = stringResource(id = R.string.animation),
                        isSelected = selectedStyle == Style.ANIMATION
                    ) { storyViewModel.setStyle(Style.ANIMATION) }

                    Spacer(Modifier.width(8.dp))

                    StyleButton(
                        title = stringResource(id = R.string.realistic),
                        isSelected = selectedStyle == Style.REALISTIC
                    ) { storyViewModel.setStyle(Style.REALISTIC) }
                }

                CommonButton(
                    text = stringResource(id = R.string.generate_storyboard),
                    backgroundColor = colorResource(id = R.color.primary),
                    contentColor = Color.White,
                    fontSize = 25,
                    horizontalPadding = 16,
                    verticalPadding = 16,
                    enabled = storyContent.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        storyViewModel.generateStory()
                    }
                )

                Text(
                    text = stringResource(id = R.string.the_storyboard_will_open_n_automatically_after_generation),
                    color = colorResource(id = R.color.text_hint),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally)
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
                            text = "Loading...",
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

@Composable
fun StyleButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    CommonButton(
        text = title,
        onClick = onClick,
        backgroundColor = if (isSelected) colorResource(id = R.color.primary)
        else colorResource(id = R.color.edit_background),
        contentColor = if (isSelected) Color.White else colorResource(id = R.color.text),
        fontSize = 18,
        horizontalPadding = 16,
        verticalPadding = 8
    )
}


