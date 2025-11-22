package com.shiyao.ai_story.model.repository

import com.shiyao.ai_story.model.entity.Asset
import com.shiyao.ai_story.model.network.ApiService
import com.shiyao.ai_story.model.network.NetworkClient
import com.shiyao.story_creat.model.dao.AssetDao
import kotlinx.coroutines.flow.Flow

/**
 * 资产数据仓库
 * （用于管理已生成的视频/封面素材）
 */
@Suppress("unused")
class AssetRepository private constructor(
    private val assetDao: AssetDao,
    private val apiService: ApiService = NetworkClient.apiService
) : BaseRepository() {


    /**
     * 单例，全局访问
     */
    companion object {
        @Volatile
        private var INSTANCE: AssetRepository? = null

        fun getInstance(assetDao: AssetDao): AssetRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AssetRepository(assetDao)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * 获取全部资产（Flow）
     */
    fun getAllAssets(): Flow<List<Asset>> =
        assetDao.getAllAssets()

    /**
     * 根据 StoryboardId 获取资产列表
     */
    fun getAssetsByStoryboardId(storyboardId: String): Flow<List<Asset>> =
        assetDao.getAssetsByStoryboardId(storyboardId)

    /**
     * 根据 ID 查询资产
     */
    suspend fun getAssetById(assetId: String): Asset? =
        handleDatabaseResult(assetDao.getAssetById(assetId))

    /**
     * 插入资产
     */
    suspend fun insertAsset(asset: Asset) =
        assetDao.insertAsset(asset)

    /**
     * 更新资产
     */
    suspend fun updateAsset(asset: Asset) =
        assetDao.updateAsset(asset)


    /**
     * 获取所有已生成的故事视频/封面资产（从服务器）
     */
    suspend fun fetchAllRemoteAssets(): List<Asset> =
        handleResponse(apiService.getAllStories())
}
