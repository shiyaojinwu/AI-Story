package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.enums.Status
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
                updateShotStatue(Status.GENERATING.value)
                shotRepository.updateShotImage(
                    shot.id,
                    GenerateShotRequest(shot.prompt, shot.narration, shot.transition)
                )
                // 开启轮询
                pollShot(shot.id)
            } catch (e: Exception) {
                // TODO: 后端 updateShotImage 接口异常时的处理逻辑待完善（目前直接标记为失败）
                updateShotStatue(Status.FAILED.value)
                _generateShotState.value = UIState.Error(e, "Generate Shot Failed")
            }
        }
    }

    fun pollShot(shotId: String, intervalMillis: Long = 2000) {
        safeLaunch {
            while (coroutineContext.isActive) {
                val shot = shotRepository.getShotPreview(shotId)
                when (shot.status) {
                    Status.GENERATING.value -> {
                        // 显示加载中
                        _generateShotState.value = UIState.Loading
                        updateShotStatue(Status.GENERATING.value)
                    }

                    Status.COMPLETED.value -> {
                        _generateShotState.value = UIState.Success("Generate Shot Success")
                        _currentEditingShot.value = shot
                        updateShotStatue(Status.COMPLETED.value)
                        break
                    }

                    Status.FAILED.value -> {
                        _generateShotState.value = UIState.Error(null, "Generate Shot Failed")
                        _currentEditingShot.value = shot
                        updateShotStatue(Status.FAILED.value)
                        break
                    }
                }
                delay(intervalMillis)
            }
        }
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
                    _shots.value = emptyList()
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
                    _shots.value = if (!shots.isNullOrEmpty()) {
                        shots.map { mapShotToUI(it, title, response.storyId) }
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("ShotViewModel", "loadShotsByNetwork error", e)
                }

                // 判断完成状态
                val currentShots = _shots.value
                if (currentShots.isNotEmpty() &&
                    currentShots.all { it.status == Status.COMPLETED.value || it.status == Status.FAILED.value }
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
            id = shot.id.toString(),
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

}