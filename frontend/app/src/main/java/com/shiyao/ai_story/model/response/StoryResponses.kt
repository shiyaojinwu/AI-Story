package com.shiyao.ai_story.model.response

import com.google.gson.annotations.SerializedName

/**
 * 创建故事响应
 */
data class CreateStoryResponse(
    val storyId: String,
    val status: String, // generating/completed/failed
    val createdAt: Long
)

/**
 * 分镜项
 */
data class ShotItem(
    val id: String,
    val sortOrder: Int,
    val title: String,
    val imageUrl: String?,
    val status: String // generating/completed/failed
)

/**
 * 故事分镜列表响应
 */
data class StoryShotsResponse(
    @SerializedName("story_id")
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