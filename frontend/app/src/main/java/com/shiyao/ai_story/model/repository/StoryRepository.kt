package com.shiyao.ai_story.model.repository
import com.shiyao.ai_story.model.dao.StoryDao
import com.shiyao.ai_story.model.entity.Story
import com.shiyao.ai_story.model.network.ApiService
import com.shiyao.ai_story.model.network.NetworkClient
import com.shiyao.ai_story.model.request.CreateStoryRequest
import com.shiyao.ai_story.model.response.CreateStoryResponse
import com.shiyao.ai_story.model.response.GenerateVideoResponse
import com.shiyao.ai_story.model.response.StoryPreviewResponse
import kotlinx.coroutines.flow.Flow

/**
 * 故事数据仓库
 * 统一管理故事数据的网络请求和本地数据库操作
 */
@Suppress("unused")
class StoryRepository private constructor(
    private val storyDao: StoryDao,
    private val apiService: ApiService = NetworkClient.apiService
) : BaseRepository() {

    /**
     * 单例，全局访问
     */
    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        // 获取实例
        fun getInstance(storyDao: StoryDao): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(storyDao)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * 获取所有故事
     */
    fun getAllStories(): Flow<List<Story>> =
        storyDao.getAllStories()

    /**
     * 根据ID获取故事
     */
    suspend fun getStoryById(storyId: String): Story? =
        handleDatabaseResult(storyDao.getStoryById(storyId))

    /**
     * 保存故事到本地数据库
     */
    suspend fun saveStory(story: Story) =
        storyDao.insertStory(story)

    /**
     * 更新本地故事
     */
    suspend fun updateStory(story: Story) =
        storyDao.updateStory(story)

    /**
     * 删除故事
     */
    suspend fun deleteStory(storyId: String) =
        storyDao.deleteStoryById(storyId)

    /**
     * 创建故事（生成故事分镜）
     * POST /api/story/create
     */
    suspend fun generateStoryboard(request: CreateStoryRequest): CreateStoryResponse =
        handleResponse(apiService.generateStoryboard(request))

    suspend fun generateStoryVideo(storyId: String): GenerateVideoResponse =
        handleResponse(apiService.generateStoryVideo(storyId))

    suspend fun getStoryPreview(storyId: String): StoryPreviewResponse =
        handleResponse(apiService.getStoryPreview(storyId))
}
