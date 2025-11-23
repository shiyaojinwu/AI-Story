package com.shiyao.ai_story.model.response

/**
 * 创建故事响应
 */
data class CreateStoryResponse(
    val storyId: String,
    val status: String,
    val createdAt: Long?
)

data class ShotItem(
    val id: String,
    val order: Int,
    val title: String,
    val imageUrl: String?,
    val status: Int
)

data class StoryShotsResponse(
    val storyId: String,
    val shots: List<ShotItem>?
)

/**
 * 生成视频响应
 */
data class GenerateVideoResponse(
    val id: String, // 视频ID
    val status: String // 状态：processing/completed/failed
)

/**
 * 视频预览轮询响应
 */
data class StoryPreviewResponse(
    val status: String,      // pending / generating / completed / failed
    val progress: Int?,      // 0 - 100
    val previewUrl: String?, // 视频预览地址
    val coverUrl: String?,   // 封面
    val error: String?       // 错误信息
)