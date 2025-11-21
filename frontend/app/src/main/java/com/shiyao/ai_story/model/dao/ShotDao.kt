package com.shiyao.ai_story.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shiyao.ai_story.model.entity.Shot
import kotlinx.coroutines.flow.Flow

/**
 * 分镜表数据访问对象
 */
@Dao
@Suppress("unused")
interface ShotDao {
    /**
     * 插入单个分镜
     */
    @Insert
    suspend fun insertShot(shot: Shot)

    /**
     * 插入多个分镜
     */
    @Insert
    suspend fun insertShots(shots: List<Shot>)

    /**
     * 更新分镜
     */
    @Update
    suspend fun updateShot(shot: Shot)

    /**
     * 根据ID获取分镜
     */
    @Query("SELECT * FROM shots WHERE id = :shotId")
    suspend fun getShotById(shotId: String): Shot?

    /**
     * 根据故事板ID获取所有分镜（按顺序排列）
     */
    @Query("SELECT * FROM shots WHERE storyboardId = :storyboardId ORDER BY sortOrder ASC")
    fun getShotsByStoryboardId(storyboardId: String): Flow<List<Shot>>

    /**
     * 根据故事板ID删除所有分镜
     */
    @Query("DELETE FROM shots WHERE storyboardId = :storyboardId")
    suspend fun deleteShotsByStoryboardId(storyboardId: String)

    /**
     * 删除分镜
     */
    @Query("DELETE FROM shots WHERE id = :shotId")
    suspend fun deleteShotById(shotId: String)
}
