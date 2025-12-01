package com.shiyao.ai_story.model.repository

import com.shiyao.ai_story.model.dao.ShotDao
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.network.ApiService
import com.shiyao.ai_story.model.network.NetworkClient
import com.shiyao.ai_story.model.request.GenerateShotRequest
import com.shiyao.ai_story.model.response.ShotDetailResponse
import com.shiyao.ai_story.model.response.ShotPreviewResponse
import com.shiyao.ai_story.model.response.StoryShotsResponse
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
class ShotRepository private constructor(
    private val shotDao: ShotDao,
    private val apiService: ApiService = NetworkClient.apiService
) : BaseRepository() {

    /**
     * 单例，全局访问
     */
    companion object {
        @Volatile
        private var INSTANCE: ShotRepository? = null

        fun getInstance(shotDao: ShotDao): ShotRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ShotRepository(shotDao)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * 获取故事所有分镜
     */
    fun getShotsByStoryId(storyId: String): Flow<List<Shot>> =
        shotDao.getShotsByStoryId(storyId)

    /**
     * 获取分镜详情
     */
    suspend fun getShotById(shotId: String): Shot? =
        handleDatabaseResult(shotDao.getShotById(shotId))

    /**
     * 插入分镜
     */
    suspend fun insertShot(shot: Shot) =
        shotDao.insertShot(shot)

    /**
     * 批量插入分镜
     */
    suspend fun insertShots(shots: List<Shot>) {
        shotDao.insertShots(shots)
    }

    /**
     * 更新分镜
     */
    suspend fun updateShot(shot: Shot) =
        shotDao.updateShot(shot)

    /**
     * 删除分镜
     */
    suspend fun deleteShot(shotId: String) =
        shotDao.deleteShotById(shotId)

    /**
     * 获取故事分镜列表
     */
    suspend fun getStoryShots(storyId: String): StoryShotsResponse =
        handleResponse(apiService.getStoryShots(storyId))

    /**
     * 获取单张分镜生成进度 / 轮询接口
     */
    suspend fun getShotPreview(shotId: String): ShotDetailResponse =
        handleResponse(apiService.getShotPreview(shotId))

    /**
     * 更新分镜图像（重生成）
     */
    suspend fun updateShotImage(shotId: String, request: GenerateShotRequest): ShotPreviewResponse =
        handleResponse(apiService.updateShotImage(shotId, request))

    /**
     * 获取分镜详情
     */
    suspend fun getShotDetail(shotId: String): ShotDetailResponse =
        handleResponse(apiService.getShotDetail(shotId))
}
