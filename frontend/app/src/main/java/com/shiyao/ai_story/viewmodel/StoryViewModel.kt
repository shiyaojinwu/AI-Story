package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.enums.BottomTab
import com.shiyao.ai_story.model.enums.Style
import com.shiyao.ai_story.model.repository.StoryRepository
import com.shiyao.ai_story.model.request.CreateStoryRequest
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
     * 返回 storyId 给 UI,跳转到分镜页
     */
    fun generateStory() {
        safeLaunch {
            _generateStoryState.value = UIState.Loading

            try {
                val createStoryResponse = storyRepository.generateStoryboard(
                    CreateStoryRequest(
                        content = storyContent.value,
                        style = selectedStyle.value.name
                    )
                )
                _generateStoryState.value = UIState.Success(createStoryResponse.storyId)
            } catch (e: Exception) {
                Log.e("StoryViewModel", "generateStory error", e)
                _generateStoryState.value = UIState.Error(e, e.message)
            }
        }
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
}
