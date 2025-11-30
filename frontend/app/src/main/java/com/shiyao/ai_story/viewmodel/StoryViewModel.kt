package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.enums.BottomTab
import com.shiyao.ai_story.model.enums.Style
import com.shiyao.ai_story.model.repository.StoryRepository
import com.shiyao.ai_story.model.request.CreateStoryRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 故事生成 ViewModel
 */
class StoryViewModel(private val storyRepository: StoryRepository) : BaseViewModel() {

    // 当前选择的风格
    private val _selectedStyle = MutableStateFlow(Style.MOVIE)
    val selectedStyle: MutableStateFlow<Style> = _selectedStyle

    // 输入的故事文本
    private val _storyContent = MutableStateFlow("")
    val storyContent: StateFlow<String> = _storyContent

    // 故事标题
    private val _storyTitle = MutableStateFlow("")
    val storyTitle: StateFlow<String> = _storyTitle

    // 底部导航状态
    private val _bottomNavSelected = MutableStateFlow(BottomTab.CREATE)
    val bottomNavSelected: MutableStateFlow<BottomTab> = _bottomNavSelected

    private val _generateStoryState = MutableStateFlow<UIState<String>>(UIState.Initial)
    val generateStoryState: StateFlow<UIState<String>> = _generateStoryState
    
    private var pollingJob: Job? = null
    
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
     * 轮询状态，每1秒一次，最多20次，直到返回 completed 或 failed
     */
    fun generateStory() {
        // 取消之前的轮询任务
        pollingJob?.cancel()
        
        safeLaunch {
            _generateStoryState.value = UIState.Loading

            try {
                // 将 Style 枚举转换为小写字符串（API需要：movie/animation/realistic）
                val styleValue = selectedStyle.value.name.lowercase()
                val createStoryResponse = storyRepository.generateStoryboard(
                    CreateStoryRequest(
                        content = storyContent.value,
                        style = styleValue
                    )
                )
                
                // 检查初始状态
                when (createStoryResponse.status.lowercase()) {
                    "completed" -> {
                        // 已经完成，直接跳转
                        _generateStoryState.value = UIState.Success(createStoryResponse.storyId)
                    }
                    "failed" -> {
                        // 失败，报错
                        _generateStoryState.value = UIState.Error(
                            Exception("Story generation failed"),
                            "加载失败"
                        )
                    }
                    "generating" -> {
                        // 生成中，开始轮询
                        pollingJob = safeLaunchJob {
                            pollStoryStatus(createStoryResponse.storyId, maxAttempts = 20)
                        }
                    }
                    else -> {
                        // 未知状态，开始轮询
                        pollingJob = safeLaunchJob {
                            pollStoryStatus(createStoryResponse.storyId, maxAttempts = 20)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "generateStory error", e)
                _generateStoryState.value = UIState.Error(e, e.message ?: "生成失败")
            }
        }
    }
    
    /**
     * 轮询故事状态
     * @param storyId 故事ID
     * @param maxAttempts 最大轮询次数，默认20次
     */
    private suspend fun pollStoryStatus(storyId: String, maxAttempts: Int = 20) {
        var attempts = 0
        while (attempts < maxAttempts) {
            try {
                delay(1000) // 等待1秒
                attempts++
                
                // 查询故事状态
                val statusResponse = storyRepository.getStoryStatus(storyId)
                
                when (statusResponse.status.lowercase()) {
                    "completed" -> {
                        // 状态为 completed，跳转页面
                        _generateStoryState.value = UIState.Success(storyId)
                        return
                    }
                    "failed" -> {
                        // 状态为 failed，报错
                        _generateStoryState.value = UIState.Error(
                            Exception("Story generation failed"),
                            "加载失败"
                        )
                        return
                    }
                    "generating" -> {
                        // 继续轮询
                        Log.d("StoryViewModel", "Polling attempt $attempts/$maxAttempts, status: generating")
                    }
                    else -> {
                        Log.w("StoryViewModel", "Unknown status: ${statusResponse.status}")
                    }
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "pollStoryStatus error", e)
                // 网络错误时继续重试
            }
        }
        
        // 20次后仍未完成，报错超时
        _generateStoryState.value = UIState.Error(
            Exception("Timeout"),
            "请求超时，请稍后重试"
        )
    }
    
    /**
     * 停止轮询
     */
    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }


    fun setStoryTitle(storyId: String): String {
        safeLaunch {
            // TODO 获取故事标题
            _storyTitle.value = "生成的故事"
        }
        return _storyTitle.value
    }

    fun setBottomNavSelected(item: BottomTab) {
        _bottomNavSelected.value = item
    }

    fun clearGenerateState() {
        _generateStoryState.value = UIState.Initial
    }
}
