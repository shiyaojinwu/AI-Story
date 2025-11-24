package com.shiyao.ai_story.model.response

/**
 * 分镜生成轮询响应
 */
data class ShotPreviewResponse(
    val status: String // processing / completed / failed
)

/**
 * 分镜详情响应
 */
data class ShotDetailResponse(
    val id: String,
    val title: String,
    val prompt: String,
    val imageUrl: String?,
    val transition: String,
    val narration: String,
    val status: String
)
