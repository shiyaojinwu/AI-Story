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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * ⚠️ 新增：用于分镜详情页编辑的状态
 * 存储用户在详情页修改的临时数据
 */
data class ShotEditingState(
    val shotId: String = "",
    val imageUrl: String? = null,
    val description: String = "",
    val transition: String = "Ken Burns Effect", // 默认值
    val narration: String = "",
)

/**
 * 分镜 ViewModel
 */
class ShotViewModel(private val shotRepository: ShotRepository) : BaseViewModel() {
    private var pollingJob: Job? = null

    // 所有分镜是否完成
    private val _allShotsCompleted = MutableStateFlow(false)
    val allShotsCompleted: StateFlow<Boolean> = _allShotsCompleted.asStateFlow()

    private val _shots = MutableStateFlow<List<ShotUI>>(emptyList())
    val shots: StateFlow<List<ShotUI>> = _shots

    // ⚠️ 关键修改点：使用一个公共的视频 URL 作为模拟路径
    // 如果您有自己的视频链接，请替换此 URL。
    private val MOCK_VIDEO_PATH =
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4"

    // ⚠️ 新增：用于管理分镜详情页的编辑状态
    private val _currentEditingShot = MutableStateFlow<ShotEditingState?>(null)
    val currentEditingShot: StateFlow<ShotEditingState?> = _currentEditingShot.asStateFlow()

    // ⚠️ 新增：预览视频的路径，用于 PreviewScreen
    private val _previewVideoPath = MutableStateFlow<String?>(null)
    val previewVideoPath: StateFlow<String?> = _previewVideoPath.asStateFlow()

    /**
     * ⚠️ 新增：选择分镜进行编辑，并设置初始状态
     */
    fun selectShotForEditing(shotUI: ShotUI) {
        // 在这里，我们将 ShotUI 的信息映射到 ShotEditingState
        _currentEditingShot.value = ShotEditingState(
            shotId = shotUI.id,
            imageUrl = shotUI.imageUrl,
            description = shotUI.prompt ?: "A misty forest at dawn with a tent", // 使用 prompt 作为描述
            transition = "Ken Burns Effect", // 模拟默认值
            narration = "Here is the narration text...", // 模拟默认值
        )
    }

    /**
     * ⚠️ 新增：更新当前编辑分镜的描述
     */
    fun updateShotDescription(newDesc: String) {
        _currentEditingShot.update { current ->
            current?.copy(description = newDesc)
        }
    }

    /**
     * ⚠️ 新增：更新当前编辑分镜的转场
     */
    fun updateShotTransition(newTransition: String) {
        _currentEditingShot.update { current ->
            current?.copy(transition = newTransition)
        }
    }

    /**
     * ⚠️ 新增：更新当前编辑分镜的旁白
     */
    fun updateShotNarration(newNarration: String) {
        _currentEditingShot.update { current ->
            current?.copy(narration = newNarration)
        }
    }

    /**
     * ⚠️ 新增：确认修改/修改完成 (保存更改)
     */
    fun saveShotChanges() {
        val updatedShotState = _currentEditingShot.value ?: return

        // 实际应用中，这里应该调用 shotRepository.updateShot() 来保存到数据库或网络
        Log.d("ShotViewModel", "Saving changes for Shot ID: ${updatedShotState.shotId}")

        // 模拟更新主列表 (仅更新 prompt/description)
        _shots.update { list ->
            list.map { shot ->
                if (shot.id == updatedShotState.shotId) {
                    // 假设更新了描述
                    shot.copy(prompt = updatedShotState.description)
                } else {
                    shot
                }
            }
        }
    }

    /**
     * ⚠️ 新增：生成最终视频
     */
    fun generateVideo(storyId: String) {
        // 实际应用中：调用 API 开始视频生成，并设置轮询
        // 这里：直接模拟生成成功，并设置预览路径为公共 URL
        _previewVideoPath.value = MOCK_VIDEO_PATH
        Log.i(
            "ShotViewModel",
            "Video generated successfully for story ID: $storyId, Path: $MOCK_VIDEO_PATH"
        )
    }

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
                    currentShots.all { it.status == ShotStatus.COMPLETED.value || it.status == ShotStatus.FAILED.value }
                ) {
                    Log.i("ShotViewModel", "所有分镜已完成（或部分失败），停止轮询")
                    _allShotsCompleted.value = true
                    break
                } else {
                    // 未完成 → 等待
                    Log.i("ShotViewModel", "分镜未完成，继续轮询...")
                    _allShotsCompleted.value = false
                    delay(intervalMillis)
                }
            }
        }
        return pollingJob!!
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun mapShotToUI(shot: ShotItem, title: String, storyId: String): ShotUI {
        return ShotUI(
            id = shot.shotId,
            storyId = storyId,
            storyTitle = title,
            title = shot.title,
            sortOrder = shot.sortOrder,
            prompt = shot.prompt,
            imageUrl = shot.imageUrl,
            status = shot.status
        )
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
                title = "分镜 1: 帐篷",
                storyId = storyId,
                sortOrder = 1,
                prompt = "Camp in the morning fog",
                imageUrl = "https://www.keaitupian.cn/cjpic/frombd/0/253/4061721412/2857814056.jpg",
                status = "completed"
            ),
            Shot(
                id = "shot_002",
                title = "分镜 2: 远足者",
                storyId = storyId,
                sortOrder = 2,
                prompt = "Hikers in the mist",
                imageUrl = "https://ts1.tc.mm.bing.net/th/id/R-C.987f582c510be58755c4933cda68d525",
                status = "completed" // 确保状态是 completed 才能显示
            ),
            Shot(
                id = "shot_003",
                title = "分镜 3: 山顶",
                storyId = storyId,
                sortOrder = 3,
                prompt = "View from the mountain top",
                imageUrl = "https://images.pexels.com/photos/1684880/pexels-photo-1684880.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                status = "completed"
            ),
            Shot(
                id = "shot_004",
                title = "分镜 4: 森林小径",
                storyId = storyId,
                sortOrder = 4,
                prompt = "Path in the deep forest",
                imageUrl = "https://images.pexels.com/photos/15286/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                status = "completed"
            ),
            Shot(
                id = "shot_005",
                title = "分镜 5: 星空",
                storyId = storyId,
                sortOrder = 5,
                prompt = "Night sky with stars",
                imageUrl = "https://images.pexels.com/photos/1252890/pexels-photo-1252890.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                status = "completed"
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