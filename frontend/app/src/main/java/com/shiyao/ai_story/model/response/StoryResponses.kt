package com.shiyao.ai_story.model.response

import com.google.gson.annotations.SerializedName

/**
 * 创建故事响应
 */
data class CreateStoryResponse(
    val storyId: String,
    val status: String, // completed/failed
    val createdAt: String,
    val title: String?,
)

/**
 * 分镜项
 */
data class ShotItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("storyId")
    val storyId: String,
    val sortOrder: Int,
    val title: String,
    val prompt: String,
    val imageUrl: String?,
    val status: String // generating/completed/failed
)

/**
 * 故事分镜列表响应
 */
data class StoryShotsResponse(
    @SerializedName("storyId")
    val storyId: String,
    val shots: List<ShotItem>?
)

/**
 * 生成视频响应
 */
data class GenerateVideoResponse(
    val id: String, // 视频ID
)

/**
 * 视频预览轮询响应
 */
data class StoryPreviewResponse(
    val status: String,      // generating / completed / failed
    val progress: Int?,      // 0 - 100
    val previewUrl: String?, // 视频预览地址
    val coverUrl: String?,   // 封面
    val error: String?       // 错误信息
)