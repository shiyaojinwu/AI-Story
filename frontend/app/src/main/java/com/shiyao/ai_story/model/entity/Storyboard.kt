package com.shiyao.ai_story.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 故事板
 */
@Entity(
    tableName = "storyboards",
    indices=[Index("storyId")],
    foreignKeys = [
        ForeignKey(
            entity = Story::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = CASCADE
        )
    ]
)
data class Storyboard(
    @PrimaryKey val id: String,
    val storyId: String,
    val title: String,
    val createdAt: Long
)
