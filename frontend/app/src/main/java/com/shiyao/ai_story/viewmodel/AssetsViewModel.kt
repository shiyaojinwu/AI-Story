package com.shiyao.ai_story.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.shiyao.ai_story.model.entity.Asset
import com.shiyao.ai_story.model.repository.AssetRepository
import com.shiyao.ai_story.utils.VideoSaver // 👈 关键：必须导入这个工具类
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssetsViewModel(
    private val assetRepository: AssetRepository
) : ViewModel() {

    private val _assetsList = MutableStateFlow<List<Asset>>(emptyList())
    val assetsList: StateFlow<List<Asset>> = _assetsList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAsset = MutableStateFlow<Asset?>(null)
    val selectedAsset: StateFlow<Asset?> = _selectedAsset.asStateFlow()

    private val _exportState = MutableStateFlow(0)
    val exportState: StateFlow<Int> = _exportState.asStateFlow()

    private val _progressPercentage = MutableStateFlow(0)
    val progressPercentage: StateFlow<Int> = _progressPercentage.asStateFlow()

    init {
        // 调试模式：加载假数据
        //loadMockData()
        // 真实模式：
        loadAssetsFromRepository()
    }

    // 设置当前选中的资产
    fun selectAsset(asset: Asset) {
        _selectedAsset.value = asset
    }

    // 重置导出状态
    fun resetExportState() {
        _exportState.value = 0
        _progressPercentage.value = 0
    }

    /**
     * 核心功能：调用 VideoSaver 导出视频
     */
    fun exportCurrentVideo(context: Context) {
        val asset = _selectedAsset.value ?: return
        val url = asset.videoUrl

        // 校验 URL 是否为空
        if (url.isNullOrEmpty()) {
            _exportState.value = -1 // 失败
            return
        }

        viewModelScope.launch {
            _exportState.value = 1 // 状态：下载中
            _progressPercentage.value = 0 // 进度归零

            val success = VideoSaver.saveVideoToGallery(
                context = context,
                videoUrl = url,
                fileName = "Story_${asset.title}_${System.currentTimeMillis()}.mp4",
                onProgress = { progress ->
                    Log.e("DEBUG_VM", "ViewModel 收到的数字: $progress")
                    _progressPercentage.value = progress
                }
            )

            _exportState.value = if (success) 2 else -1
        }
    }


    private fun loadMockData() {
        val mocks = listOf(
            Asset(
                id = 1,
                storyId = 101,
                title = "Journey Through Woods",
                thumbnailUrl = "https://img.freepik.com/free-photo/forest-landscape-with-sun-rays_23-2147956965.jpg",
                status = 2, // 2 = completed
                videoUrl = "https://v-cdn.zjol.com.cn/280443.mp4",
                createdAt = "2023-11-28",
                duration = 15
            )
        )
        _assetsList.value = mocks
    }


    private fun loadAssetsFromRepository() {
        Log.d("DEBUG_API", "准备发起请求...")

        viewModelScope.launch {
            assetRepository.getAllAssets().collect { list ->
                _assetsList.value = list
            }
        }

        viewModelScope.launch {
            try {
                val result = assetRepository.fetchAllRemoteAssets()
                Log.d("DEBUG_API", "请求成功，拿到数据: ${result.size} 条")
                assetRepository.insertAssets(result)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DEBUG_API", "请求失败: ${e.message}")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            loadMockData()
        } else {
            val currentList = _assetsList.value
            _assetsList.value = currentList.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }
}