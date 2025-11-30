package com.shiyao.ai_story.viewmodel



import android.util.Log
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.enums.ShotStatus
import com.shiyao.ai_story.model.repository.ShotRepository
import com.shiyao.ai_story.model.response.ShotItem
import com.shiyao.ai_story.model.ui.ShotUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

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

    private val _shots = MutableStateFlow<List<ShotUI>>(emptyList())
    val shots: StateFlow<List<ShotUI>> = _shots

    // ⚠️ 关键修改点：使用一个公共的视频 URL 作为模拟路径
    // 如果您有自己的视频链接，请替换此 URL。
    private val MOCK_VIDEO_PATH = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4"

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
        Log.i("ShotViewModel", "Video generated successfully for story ID: $storyId, Path: $MOCK_VIDEO_PATH")
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
     * 加载指定 storyId 的分镜
     */
    fun loadShotsByNetwork(storyId: String, title: String) {
        safeLaunch {
            try {
                val response = shotRepository.getStoryShots(storyId)
                val shots = response.shots
                val uiList = if (!shots.isNullOrEmpty()) {
                    shots.map { mapShotToUI(it, title,response.storyId) }
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
    fun pollShotsUntilCompleted(storyId: String, title: String, intervalMillis: Long = 2000) {
        safeLaunch {
            while (true) {
                // 加载数据
                loadShotsByNetwork(storyId, title)
                // 获取当前 UI 数据
                val currentShots = _shots.value
                // 检查是否全部完成
                if (currentShots.isNotEmpty() && currentShots.all { it.status == ShotStatus.COMPLETED.value }) {
                    Log.i("ShotViewModel", "所有分镜已完成，停止轮询")
                    break
                } else {
                    Log.i("ShotViewModel", "分镜未完成，继续轮询...")
                }
                // 等待间隔
                delay(intervalMillis)
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