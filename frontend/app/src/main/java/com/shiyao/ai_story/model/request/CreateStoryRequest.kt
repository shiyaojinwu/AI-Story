package com.shiyao.ai_story.model.request

/**
 * 生成故事分镜请求体
 */
data class CreateStoryRequest(
    val content: String, // 故事文本内容
    val style: String // 生成风格：电影/动画/写实
)

