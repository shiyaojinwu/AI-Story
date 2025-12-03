package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.enums.Status
import com.shiyao.ai_story.model.enums.Style
import com.shiyao.ai_story.model.repository.StoryRepository
import com.shiyao.ai_story.model.request.CreateStoryRequest
import com.shiyao.ai_story.model.response.StoryPreviewResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 故事生成 ViewModel
 */
class StoryViewModel(private val storyRepository: StoryRepository) : BaseViewModel() {

    companion object {
        /**
         * Mock 模式开关
         * 设置为 true 时，使用 Mock 数据
         * 设置为 false 时，使用真实 API
         */
        private const val USE_MOCK_MODE = true
    }

    // 当前选择的风格
    private val _selectedStyle = MutableStateFlow(Style.MOVIE)
    val selectedStyle: MutableStateFlow<Style> = _selectedStyle

    // 输入的故事文本
    private val _storyContent = MutableStateFlow("")
    val storyContent: StateFlow<String> = _storyContent

    // 故事标题
    private val _storyTitle = MutableStateFlow("")
    val storyTitle: StateFlow<String> = _storyTitle

    // 故事生成状态
    private val _generateStoryState = MutableStateFlow<UIState<String>>(UIState.Initial)
    val generateStoryState: StateFlow<UIState<String>> = _generateStoryState

    // 视频生成状态
    private val _generateVideoState =
        MutableStateFlow<UIState<StoryPreviewResponse>>(UIState.Initial)
    val generateVideoState: StateFlow<UIState<StoryPreviewResponse>> = _generateVideoState

    // 视频生成任务
    private var videoPollingJob: Job? = null

    /**
     * 设置风格
     */
    fun setStyle(style: Style) {
        _selectedStyle.value = style
    }

    /**
     * 设置故事内容
     */
    fun setStoryContent(content: String) {
        _storyContent.value = content
    }

    /**
     * 生成故事：调 Repository 请求后端生成 story
     */
    fun generateStory() {
        safeLaunch {
            _generateStoryState.value = UIState.Loading
            try {
                val styleValue = _selectedStyle.value.name.lowercase()
                val createStoryResponse = storyRepository.generateStoryboard(
                    CreateStoryRequest(
                        content = _storyContent.value,
                        style = styleValue
                    )
                )
                // 检查初始状态
                when (createStoryResponse.status.lowercase()) {
                    Status.COMPLETED.value -> {
                        _generateStoryState.value = UIState.Success(createStoryResponse.storyId)
                        _storyTitle.value = createStoryResponse.title
                    }

                    Status.FAILED.value -> {
                        _generateStoryState.value = UIState.Error(
                            Exception("Story generation failed"),
                            "生成失败"
                        )
                    }

                    else -> {
                        Log.w(
                            "StoryViewModel",
                            "Unknown story status: ${createStoryResponse.status}"
                        )
                        _generateStoryState.value = UIState.Error(
                            Exception("Story generation failed"),
                            "生成失败"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "generateStory error", e)
                _generateStoryState.value = UIState.Error(e, e.message ?: "生成失败")
            }
        }
    }

    fun clearGenerateState() {
        _generateStoryState.value = UIState.Initial
    }

    /**
     * 生成视频：调 Repository 请求后端生成视频
     * 轮询状态，每1秒一次，最多20次，直到返回 completed 或 failed
     */
    fun generateVideo(storyId: String) {
        // 取消之前的轮询任务
        videoPollingJob?.cancel()
        safeLaunch {
            _generateVideoState.value = UIState.Loading
            try {
                if (USE_MOCK_MODE) {
                    // Mock 模式：模拟生成视频
                    mockGenerateVideo(storyId)
                } else {
                    // 真实模式：调用 POST /api/story/{id}/generate-video
                    val response = storyRepository.generateStoryVideo(storyId)
                    // 拿到视频 ID 后开始轮询预览状态（GET /api/story/{id}/preview）
                    videoPollingJob = safeLaunchJob {
                        pollVideoStatus(response.id, storyId)
                    }
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "generateVideo error", e)
                _generateVideoState.value = UIState.Error(e, e.message ?: "生成视频失败")
            }
        }
    }

    /**
     * Mock 模式：模拟生成视频
     */
    private suspend fun mockGenerateVideo(storyId: String) {
        // 模拟网络延迟
        delay(500)

        // 生成 Mock videoId
        val mockVideoId = "mock_video_${System.currentTimeMillis()}"

        // 开始模拟轮询进度
        videoPollingJob = safeLaunchJob {
            mockPollVideoStatus(mockVideoId, storyId)
        }
    }

    /**
     * Mock 模式：模拟视频生成进度轮询
     */
    private suspend fun mockPollVideoStatus(videoId: String, storyId: String) {
        // 模拟进度：从 10% 逐步增加到 100%
        val progressSteps = listOf(10, 25, 40, 55, 70, 85, 100)

        for ((index, progress) in progressSteps.withIndex()) {
            if (index > 0) {
                delay(1200) // 每1.2秒更新一次进度
            }

            Log.d("StoryViewModel", "Mock video progress: $progress%")

            // 最后一步，标记为完成
            if (progress == 100) {
                delay(500)
                _generateVideoState.value = UIState.Success(
                    StoryPreviewResponse(
                        status = Status.COMPLETED.value,
                        previewUrl = "http://flv4mp4.people.com.cn/videofile7/pvmsvideo/2023/4/14/DangWang-BoChenDi_7a0283ace3c035c20500c33dfaef44ed.mp4",
                        coverUrl = null,
                        error = null,
                        progress = null

                    )
                )
                return
            }
        }
    }

    /**
     * 轮询视频生成状态
     * @param videoId 视频ID
     * @param storyId 故事ID（用于查询预览）
     * @param maxAttempts 最大轮询次数，默认20次
     */
    private suspend fun pollVideoStatus(videoId: String, storyId: String, maxAttempts: Int = 20) {
        var attempts = 0
        while (attempts < maxAttempts) {
            try {
                if (attempts > 0) { // 第一次立即查询，之后等待1秒
                    delay(1000)
                }
                attempts++

                // 查询视频预览状态（使用 GET /api/story/{id}/preview）
                val previewResponse = storyRepository.getStoryPreview(storyId)

                when (previewResponse.status.lowercase()) {
                    Status.COMPLETED.value -> {
                        // 状态为 completed，成功
                        _generateVideoState.value = UIState.Success(previewResponse)
                        return
                    }

                    Status.FAILED.value -> {
                        // 状态为 failed，报错
                        _generateVideoState.value = UIState.Error(
                            Exception("Video generation failed"),
                            previewResponse.error ?: "视频生成失败"
                        )
                        return
                    }

                    Status.GENERATING.value -> {
                        // 继续轮询
                        Log.d(
                            "StoryViewModel",
                            "Video polling attempt $attempts/$maxAttempts, status: ${previewResponse.status}, progress: ${previewResponse.progress}%"
                        )
                    }

                    else -> {
                        Log.w("StoryViewModel", "Unknown video status: ${previewResponse.status}")
                    }
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "pollVideoStatus error", e)
            }
        }

        // 20次后仍未完成，报错超时
        _generateVideoState.value = UIState.Error(
            Exception("Timeout"),
            "请求超时，请稍后重试"
        )
    }

    fun clearGenerateVideoState() {
        _generateVideoState.value = UIState.Initial
    }
}
