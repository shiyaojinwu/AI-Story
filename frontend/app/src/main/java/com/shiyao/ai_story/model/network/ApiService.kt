package com.shiyao.ai_story.model.network

import com.shiyao.ai_story.model.entity.Asset
import com.shiyao.ai_story.model.entity.Story
import com.shiyao.ai_story.model.request.CreateStoryRequest
import com.shiyao.ai_story.model.request.GenerateShotRequest
import com.shiyao.ai_story.model.response.CreateStoryResponse
import com.shiyao.ai_story.model.response.GenerateVideoResponse
import com.shiyao.ai_story.model.response.ShotDetailResponse
import com.shiyao.ai_story.model.response.ShotPreviewResponse
import com.shiyao.ai_story.model.response.StoryPreviewResponse
import com.shiyao.ai_story.model.response.StoryShotsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API服务接口
 * 定义与后端交互的所有网络请求方法
 */
@Suppress("unused")
interface ApiService {

    /**
     * 创建故事（生成故事分镜）
     * POST /api/story/create
     */
    @POST("/api/story")
    suspend fun generateStoryboard(
        @Body request: CreateStoryRequest
    ): Response<ApiResponse<CreateStoryResponse>>
    
    /**
     * 查询故事状态（用于轮询）
     * GET /api/story/{id}/status
     */
    @GET("/api/story/{id}/status")
    suspend fun getStoryStatus(
        @Path("id") storyId: String
    ): Response<ApiResponse<CreateStoryResponse>>

    /**
     * 获取故事分镜列表
     */
    @GET("/api/story/{id}/shots")
    suspend fun getStoryShots(
        @Path("id") storyId: String
    ): Response<ApiResponse<StoryShotsResponse>>

    /**
     * 获取单张分镜生成进度 / 轮询接口
     */
    @GET("/api/shot/{id}/preview")
    suspend fun getShotPreview(
        @Path("id") shotId: String
    ): Response<ApiResponse<ShotPreviewResponse>>

    /**
     * 获取分镜详情
     */
    @GET("/api/shot/{id}")
    suspend fun getShotDetail(
        @Path("id") shotId: String
    ): Response<ApiResponse<ShotDetailResponse>>

    /**
     * 更新单张分镜图像
     */
    @POST("/api/shot/{id}/update")
    suspend fun updateShotImage(
        @Path("id") shotId: String,
        @Body request: GenerateShotRequest
    ): Response<ApiResponse<ShotPreviewResponse>>

    /**
     * 生成视频
     */
    @POST("/api/story/{id}/generate-video")
    suspend fun generateStoryVideo(
        @Path("id") storyId: String
    ): Response<ApiResponse<GenerateVideoResponse>>

    /**
     * 视频预览轮询
     */
    @GET("/api/story/{id}/preview")
    suspend fun getStoryPreview(
        @Path("id") storyId: String
    ): Response<ApiResponse<StoryPreviewResponse>>

    /**
     * 获取所有视频资产
     */
    @GET("/api/story/all")
    suspend fun getAllStories(): Response<ApiResponse<List<Asset>>>

    /**
     * 获取故事详情
     */
    @GET("/api/story/{storyId}")
    suspend fun getStoryDetail(
        @Path("storyId") storyId: String
    ): Response<ApiResponse<Story>>
}
