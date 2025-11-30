package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.enums.ShotStatus
import com.shiyao.ai_story.model.repository.ShotRepository
import com.shiyao.ai_story.model.response.ShotItem
import com.shiyao.ai_story.model.ui.ShotUI
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * 分镜 ViewModel
 */
class ShotViewModel(private val shotRepository: ShotRepository) : BaseViewModel() {
    private var pollingJob: Job? = null
    private val _shots = MutableStateFlow<List<ShotUI>>(emptyList())
    val shots: StateFlow<List<ShotUI>> = _shots

    /**
     * 加载指定 storyId 的分镜
     */
    fun loadShotsBySql(storyId: String, title: String) {
        safeLaunch {
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

    /**
     * 加载指定 storyId 的分镜
     */
    fun loadShotsByNetwork(storyId: String, title: String) {
        safeLaunch {
            try {
                val response = shotRepository.getStoryShots(storyId)
                val shots = response.shots
                val uiList = if (!shots.isNullOrEmpty()) {
                    shots.map { mapShotToUI(it, title, response.storyId) }
                } else {
                    createMockShots(storyId).map { mapShotToUI(it, title) }
                }
                _shots.value = uiList
            } catch (e: Exception) {
                Log.e("ShotViewModel", "loadShotsByNetwork error", e)
                _shots.value = emptyList() // 或者处理错误状态
            }
        }
    }

    private fun mapShotToUI(shot: ShotItem, title: String, storyId: String): ShotUI {
        return ShotUI(
            id = shot.id,
            storyId = storyId,
            storyTitle = title,
            title = shot.title,
            sortOrder = shot.sortOrder,
            prompt = null,
            imageUrl = shot.imageUrl,
            status = shot.status
        )
    }

    /**
     * 轮询加载网络分镜，直到全部完成
     */
    fun pollShotsUntilCompleted(storyId: String, title: String, intervalMillis: Long = 2000): Job {
        pollingJob?.cancel()
        pollingJob = safeLaunchJob {
            while (coroutineContext.isActive) {
                // 加载数据
                try {
                    val response = shotRepository.getStoryShots(storyId)
                    val shots = response.shots
                    val uiList = if (!shots.isNullOrEmpty()) {
                        shots.map { mapShotToUI(it, title, response.storyId) }
                    } else {
                        createMockShots(storyId).map { mapShotToUI(it, title) }
                    }
                    _shots.value = uiList
                } catch (e: Exception) {
                    Log.e("ShotViewModel", "loadShotsByNetwork error", e)
                }
                // 判断完成状态
                val currentShots = _shots.value
                if (currentShots.isNotEmpty() &&
                    currentShots.all { it.status == ShotStatus.COMPLETED.value }
                ) {
                    Log.i("ShotViewModel", "所有分镜已完成，停止轮询")
                    break
                }
                // 未完成 → 等待
                Log.i("ShotViewModel", "分镜未完成，继续轮询...")
                delay(intervalMillis)
            }
        }
        return pollingJob!!
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
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
                status = "failed"
            )
        )
    }

    /**
     * 刷新分镜列表
     */
    fun refresh(storyId: String, title: String) {
        _shots.value = emptyList()
        loadShotsBySql(storyId, title)
    }
}
