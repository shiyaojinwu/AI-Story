package com.shiyao.ai_story.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 故事实体
 */
@Entity(tableName = "stories")
data class Story(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val style: String, // 电影/动画/写实
    val createdAt: Long,
    val updatedAt: Long
)
