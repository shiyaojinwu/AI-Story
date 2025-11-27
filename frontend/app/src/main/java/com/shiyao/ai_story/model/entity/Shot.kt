package com.shiyao.ai_story.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * 分镜实体
 */
@Entity(
    tableName = "shots",
    indices = [Index(value = ["storyId"])],
    foreignKeys = [
        ForeignKey(
            entity = Story::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = CASCADE
        )
    ]
)
data class Shot(
    @PrimaryKey val id: String,
    val storyId: String,
    val title: String,
    val sortOrder: Int,
    val prompt: String,
    val imageUrl: String? = null,
    val narration: String? = null,
    val transition: String = "Crossfade", // 转场效果：Ken Burns / Crossfade / Volume Mix
    val status: String = "generating", // 状态： generating / completed / failed
    val createdAt: String? = Instant.now().toString(),
    val updatedAt: String? = Instant.now().toString()
)
