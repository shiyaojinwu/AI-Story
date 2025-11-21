package com.shiyao.ai_story.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shiyao.ai_story.model.entity.Story
import kotlinx.coroutines.flow.Flow

/**
 * 故事表数据访问对象
 */
@Dao
@Suppress("unused")
interface StoryDao {
    /**
     * 插入故事
     */
    @Insert
    suspend fun insertStory(story: Story)

    /**
     * 更新故事
     */
    @Update
    suspend fun updateStory(story: Story)

    /**
     * 根据ID获取故事
     */
    @Query("SELECT * FROM stories WHERE id = :storyId")
    suspend fun getStoryById(storyId: String): Story?

    /**
     * 获取所有故事（Flow类型，支持实时数据更新）
     */
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<Story>>

    /**
     * 删除故事
     */
    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStoryById(storyId: String)
}
