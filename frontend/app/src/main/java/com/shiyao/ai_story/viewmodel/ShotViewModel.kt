package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.enums.ShotStatus
import com.shiyao.ai_story.model.repository.ShotRepository
import com.shiyao.ai_story.model.request.GenerateShotRequest
import com.shiyao.ai_story.model.response.ShotDetailResponse
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
 * 分镜 ViewModel
 */
class ShotViewModel(private val shotRepository: ShotRepository) : BaseViewModel() {
    private var pollingJob: Job? = null

    // 所有分镜是否完成或否失败
    private val _allShotsCompletedOrFail = MutableStateFlow(false)
    val allShotsCompletedOrFail: StateFlow<Boolean> = _allShotsCompletedOrFail.asStateFlow()

    private val _shots = MutableStateFlow<List<ShotUI>>(emptyList())
    val shots: StateFlow<List<ShotUI>> = _shots

    // ⚠️ 关键修改点：使用一个公共的视频 URL 作为模拟路径
    // 如果您有自己的视频链接，请替换此 URL。
    private val MOCK_VIDEO_PATH =
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4"

    // ⚠️ 新增：用于管理分镜详情页的编辑状态
    private val _currentEditingShot = MutableStateFlow<ShotDetailResponse?>(null)
    val currentEditingShot: StateFlow<ShotDetailResponse?> = _currentEditingShot.asStateFlow()

    // ⚠️ 新增：预览视频的路径，用于 PreviewScreen
    private val _previewVideoPath = MutableStateFlow<String?>(null)
    val previewVideoPath: StateFlow<String?> = _previewVideoPath.asStateFlow()

    private val _generateShotState = MutableStateFlow<UIState<String>>(UIState.Initial)
    val generateShotState: StateFlow<UIState<String>> = _generateShotState

    fun clearGenerateState() {
        _generateShotState.value = UIState.Initial
    }

    /**
     * 用于退出后重置
     */
    fun refreshCurrentEditingShot() {
        _currentEditingShot.value = null
    }

    /**
     * ⚠️ 新增：更新当前编辑分镜的描述
     */
    fun updateShotDescription(newDesc: String) {
        _currentEditingShot.update { current ->
            current?.copy(prompt = newDesc)
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
     * ⚠️ 新增：更新当前编辑分镜的状态
     */
    fun updateShotStatue(newStatus: String) {
        _currentEditingShot.update { current ->
            current?.copy(status = newStatus)
        }
    }
    /**
     * 更新并重新 分镜图片
     */
    fun updateAndGenerateShot() {
        val shot = _currentEditingShot.value
        if (shot == null) return
        safeLaunch {
            // 显示加载中
            _generateShotState.value = UIState.Loading
            // 生成
            try {
                updateShotStatue(ShotStatus.GENERATING.value)
                shotRepository.updateShotImage(
                    shot.id,
                    GenerateShotRequest(shot.prompt, shot.narration, shot.transition)
                )
                // 开启轮询
                pollShot(shot.id)
            }catch (e: Exception){
                // mock数据
                updateShotStatue(ShotStatus.FAILED.value)
                _generateShotState.value = UIState.Error(e, "Generate Shot Failed")
            }
        }
    }

    fun pollShot(shotId: String, intervalMillis: Long = 2000) {
        safeLaunch {
            while (coroutineContext.isActive) {
                val shot = shotRepository.getShotPreview(shotId)
                when (shot.status) {
                    ShotStatus.GENERATING.value -> {
                        // 显示加载中
                        _generateShotState.value = UIState.Loading
                        updateShotStatue(ShotStatus.GENERATING.value)
                    }

                    ShotStatus.COMPLETED.value -> {
                        _generateShotState.value = UIState.Success("Generate Shot Success")
                        _currentEditingShot.value = shot
                        updateShotStatue(ShotStatus.COMPLETED.value)
                        break
                    }

                    ShotStatus.FAILED.value -> {
                        _generateShotState.value = UIState.Error(null, "Generate Shot Failed")
                        _currentEditingShot.value = shot
                        updateShotStatue(ShotStatus.FAILED.value)
                        break
                    }
                }
                delay(intervalMillis)
            }
        }
    }

    /**
     * ⚠️ 新增：生成最终视频（已弃用，视频生成现在在 StoryViewModel 中处理）
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
     * 设置预览视频路径
     */
    fun setPreviewVideoPath(videoUrl: String) {
        _previewVideoPath.value = videoUrl
        Log.d("ShotViewModel", "Preview video path set: $videoUrl")
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

    fun getShotDetail(shotId: String) {
        safeLaunch {
            val shotDetailResponse = try {
                shotRepository.getShotDetail(shotId)
            } catch (e: Exception) {
                Log.e("ShotViewModel", "getShotDetail error", e)
                ShotDetailResponse(
                    shotId,
                    "分镜 1: 帐篷",
                    "Camp in the morning fog",
                    "https://www.keaitupian.cn/cjpic/frombd/0/253/4061721412/2857814056.jpg",
                    "Crossfade",
                    "帐篷",
                    "completed"
                )
            }
            _currentEditingShot.value = shotDetailResponse
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
                    _allShotsCompletedOrFail.value = true
                    break
                } else {
                    // 未完成 → 等待
                    Log.i("ShotViewModel", "分镜未完成，继续轮询...")
                    _allShotsCompletedOrFail.value = false
                    delay(intervalMillis)
                }
            }
        }
        return pollingJob!!
    }

    /**
     * 刷新分镜列表
     */
    fun refreshShots() {
        _shots.value = emptyList()
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
        // 占位图 URL
        val placeholderImageUrl = "https://www.keaitupian.cn/cjpic/frombd/0/253/4061721412/2857814056.jpg"

        return listOf(
            Shot(
                id = "shot_001",
                title = "Shot 1",
                storyId = storyId,
                sortOrder = 1,
                prompt = "Opening scene in the morning fog",
                imageUrl = placeholderImageUrl,
                status = "completed"
            ),
            Shot(
                id = "shot_002",
                title = "Shot 2",
                storyId = storyId,
                sortOrder = 2,
                prompt = "Hikers walking through the forest",
                imageUrl = placeholderImageUrl,
                status = "completed"
            ),
            Shot(
                id = "shot_003",
                title = "Shot 3",
                storyId = storyId,
                sortOrder = 3,
                prompt = "Mountain peak at sunset",
                imageUrl = placeholderImageUrl,
                status = "completed"
            ),
            Shot(
                id = "shot_004",
                title = "Shot 4",
                storyId = storyId,
                sortOrder = 4,
                prompt = "Campfire scene at night",
                imageUrl = placeholderImageUrl,
                status = "completed"
            )
        )
    }
}