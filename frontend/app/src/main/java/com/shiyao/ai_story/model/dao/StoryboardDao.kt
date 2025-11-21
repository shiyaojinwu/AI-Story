package com.shiyao.ai_story.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shiyao.ai_story.model.entity.Storyboard
import kotlinx.coroutines.flow.Flow

/**
 * 故事板表数据访问对象
 */
@Dao
@Suppress("unused")
interface StoryboardDao {
    /**
     * 插入故事板
     */
    @Insert
    suspend fun insertStoryboard(storyboard: Storyboard)

    /**
     * 更新故事板
     */
    @Update
    suspend fun updateStoryboard(storyboard: Storyboard)

    /**
     * 根据ID获取故事板
     */
    @Query("SELECT * FROM storyboards WHERE id = :storyboardId")
    suspend fun getStoryboardById(storyboardId: String): Storyboard?

    /**
     * 根据故事ID获取所有故事板
     */
    @Query("SELECT * FROM storyboards WHERE storyId = :storyId ORDER BY createdAt DESC")
    fun getStoryboardsByStoryId(storyId: String): Flow<List<Storyboard>>

    /**
     * 删除故事板
     */
    @Query("DELETE FROM storyboards WHERE id = :storyboardId")
    suspend fun deleteStoryboardById(storyboardId: String)
}
