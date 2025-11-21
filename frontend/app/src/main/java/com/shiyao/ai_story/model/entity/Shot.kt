package com.shiyao.ai_story.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 分镜实体
 */
@Entity(
    tableName = "shots",
    indices = [Index(value = ["storyboardId"])],
    foreignKeys = [
        ForeignKey(
            entity = Storyboard::class,
            parentColumns = ["id"],
            childColumns = ["storyboardId"],
            onDelete = CASCADE
        )
    ]
)
data class Shot(
    @PrimaryKey val id: String,
    val storyboardId: String,
    val sortOrder: Int,
    val prompt: String,
    val imageUrl: String? = null,
    val narration: String? = null,
    val transition: String = "Crossfade", // 转场效果：Ken Burns / Crossfade / Volume Mix
    val status: String = "pending", // 状态：pending / generating / completed / failed
    val createdAt: Long
)
