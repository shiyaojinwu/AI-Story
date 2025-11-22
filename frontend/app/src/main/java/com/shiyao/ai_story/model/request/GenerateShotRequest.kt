package com.shiyao.ai_story.model.request

/**
 * 生成单分镜请求体
 */
data class GenerateShotRequest(
    val prompt: String, // 图像生成提示词
    val narration: String, // 分镜的旁白
    val transition: String, // 转场效果
)

