package com.shiyao.ai_story.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shiyao.ai_story.R

/**
 * 带网络图片+标签的通用卡片组件（仅支持网络URL图片）
 * @param title 卡片标题
 * @param tag 卡片标签（如"Generated"）
 * @param imageUrl 网络图片地址（必传，无需空值校验）
 * @param modifier 布局修饰符
 * @param backgroundColor 卡片背景色
 * @param imageHeight 图片区域高度（默认180dp）
 */
@Composable
fun CommonCard(
    modifier: Modifier = Modifier,
    title: String,
    tag: String? = null,
    content: String? = null,
    imageUrl: String,
    backgroundColor: Color = colorResource(id = R.color.card_background),
    imageHeight: Dp = 180.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 20.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = null, // 错误图
                    placeholder = null // 加载中占位
                )

                tag?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.tag_text),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                color = colorResource(id = R.color.tag_background),
                                shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                content?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.text_hint),
                    )
                }
            }
        }
    }
}

// 基础单张预览（保留原预览）
@Preview(showBackground = true, name = "Single Card")
@Composable
fun CommonCard_Single_Preview() {
    CommonCard(
        title = "Camp in the mountains",
        tag = "Generated",
        imageUrl = "https://www.keaitupian.cn/cjpic/frombd/0/253/4061721412/2857814056.jpg"
    )
}

// 横向滑动预览（多张图片展示）
@Preview(showBackground = true, name = "Horizontal Scroll Preview", widthDp = 360, heightDp = 500)
@Composable
fun CommonCard_Horizontal_Preview() {
    val testImageUrls = listOf(
        "https://www.keaitupian.cn/cjpic/frombd/0/253/4061721412/2857814056.jpg",
        "https://ts1.tc.mm.bing.net/th/id/R-C.987f582c510be58755c4933cda68d525?rik=C0D21hJDYvXosw&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fwallpaper%2f1305%2f16%2fc4%2f20990657_1368686545122.jpg&ehk=netN2qzcCVS4ALUQfDOwxAwFcy41oxC%2b0xTFvOYy5ds%3d&risl=&pid=ImgRaw&r=0",
        "https://ts1.tc.mm.bing.net/th/id/R-C.8bbf769b39bb26eefb9b6de51c23851d?rik=crTnc5i8A%2b8p7A&riu=http%3a%2f%2fpicview.iituku.com%2fcontentm%2fzhuanji%2fimg%2f202207%2f09%2fe7196ac159f7cf2b.jpg%2fnu&ehk=DYPLVpoNAXLj5qzwgR5vHf9DladFh%2b34s4UcuP3Kn6E%3d&risl=&pid=ImgRaw&r=0",
    )
    val testTitles = listOf(
        "test1",
        "test2",
        "test3",
    )
    val testTags = listOf("Generated", "Generated", "Generated")

    val pagerState = rememberPagerState(pageCount = { testImageUrls.size })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 16.dp
            ) { page ->
                CommonCard(
                    title = testTitles[page],
                    tag = testTags[page],
                    imageUrl = testImageUrls[page],
                    backgroundColor = colorResource(id = R.color.card_background),
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { index ->
                    val color = if (pagerState.currentPage == index) {
                        Color(0xFFFFFFFF)
                    } else {
                        Color.Gray.copy(alpha = 0.5f)
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}