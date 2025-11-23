package com.shiyao.ai_story.viewmodel

import androidx.lifecycle.viewModelScope
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.repository.ShotRepository
import com.shiyao.ai_story.model.ui.ShotUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 分镜 ViewModel
 */
class ShotViewModel(private val shotRepository: ShotRepository) : BaseViewModel() {

    private val _shots = MutableStateFlow<List<ShotUI>>(emptyList())
    val shots: StateFlow<List<ShotUI>> = _shots

    /**
     * 加载指定 storyId 的分镜
     */
    fun loadShots(storyId: String, title: String) {
        viewModelScope.launch(Dispatchers.IO) {

            shotRepository.getShotsByStoryId(storyId).collectLatest { dbShots ->

                if (dbShots.isNotEmpty()) {
                    _shots.value = dbShots.map { mapShotToUI(it, title) }

                } else {
                    // DB 为空： mock
                    val mockShots = createMockShots(storyId)
                    _shots.value = mockShots.map { mapShotToUI(it, title) }
                }
            }
        }
    }


    private fun mapShotToUI(shot: Shot, title: String): ShotUI {
        return ShotUI(
            id = shot.id,
            storyId = shot.storyId,
            storyTitle = title,
            title = shot.title,
            sortOrder = shot.sortOrder,
            prompt = shot.prompt,
            imageUrl = shot.imageUrl,
            status = shot.status
        )
    }


    /**
     * 生成 mock 数据
     */
    private fun createMockShots(storyId: String): List<Shot> {
        return listOf(
            Shot(
                id = "shot_001",
                title = "Untitled Storyboard",
                storyId = storyId,
                sortOrder = 1,
                prompt = "Camp in the morning fog",
                imageUrl = "https://www.keaitupian.cn/cjpic/frombd/0/253/4061721412/2857814056.jpg",
                status = "completed"
            ),
            Shot(
                id = "shot_002",
                title = "Untitled Storyboard",
                storyId = storyId,
                sortOrder = 2,
                prompt = "Hikers in the mist",
                imageUrl = "https://ts1.tc.mm.bing.net/th/id/R-C.987f582c510be58755c4933cda68d525",
                status = "completed"
            )
        )
    }

    /**
     * 刷新分镜列表
     */
    fun refresh(storyId: String, title: String) {
        _shots.value = emptyList()
        loadShots(storyId, title)
    }
}
