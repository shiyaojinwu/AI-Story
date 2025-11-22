package com.shiyao.story_creat.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shiyao.ai_story.model.entity.Asset
import kotlinx.coroutines.flow.Flow

/**
 * 资产表数据访问对象
 */
@Dao
@Suppress("unused")
interface AssetDao {
    /**
     * 插入资产
     */
    @Insert
    suspend fun insertAsset(asset: Asset)

    /**
     * 更新资产
     */
    @Update
    suspend fun updateAsset(asset: Asset)

    /**
     * 根据ID获取资产
     */
    @Query("SELECT * FROM asset WHERE id = :assetId")
    suspend fun getAssetById(assetId: String): Asset?

    /**
     * 获取所有资产
     */
    @Query("SELECT * FROM asset ORDER BY createdAt DESC")
    fun getAllAssets(): Flow<List<Asset>>

    /**
     * 根据故事ID获取资产
     */
    @Query("SELECT * FROM asset WHERE storyId = :storyboardId")
    fun getAssetsByStoryboardId(storyboardId: String): Flow<List<Asset>>
}
