package com.shiyao.ai_story.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 故事实体
 */
@Entity(tableName = "stories")
data class Story(
    @PrimaryKey val id: String,
    val content: String,
    val style: String, // 电影/动画/写实
    val status: String, // 状态：generating / completed / failed
    val createdAt: Long?=System.currentTimeMillis()
)
