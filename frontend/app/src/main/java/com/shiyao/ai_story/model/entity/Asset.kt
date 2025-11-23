package com.shiyao.ai_story.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * 资产实体类
 * 用于在资产库中展示已完成的视频资产
 */
@Entity(
    tableName = "asset"
)
data class Asset(
    @PrimaryKey val id: String,
    val storyId: String,
    val title: String,
    val thumbnailUrl: String? = null,
    val videoUrl: String? = null,
    val duration: Int? = null,
    val status: String, // 状态：generating / completed / failed
    val createdAt: Long?=System.currentTimeMillis()
)
