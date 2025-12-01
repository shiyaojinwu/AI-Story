package com.shiyao.ai_story.model.ui

/**
 * UI 使用的 Shot 对象（包含标题）
 */
data class ShotUI(
    val id: String,
    val storyId: String,
    val storyTitle: String,
    val title: String,
    val sortOrder: Int,
    val prompt: String,
    val imageUrl: String?,
    val status: String?
)
