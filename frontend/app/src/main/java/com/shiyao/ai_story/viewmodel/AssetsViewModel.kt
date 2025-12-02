package com.shiyao.ai_story.viewmodel

import android.util.Log
import com.shiyao.ai_story.model.entity.Asset
import com.shiyao.ai_story.model.repository.AssetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssetsViewModel(
    private val assetRepository: AssetRepository
) : BaseViewModel() {

    // UI 观察的数据流
    private val _assetsList = MutableStateFlow<List<Asset>>(emptyList())
    val assetsList: StateFlow<List<Asset>> = _assetsList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 1. 加载假数据 (用于调试 UI)
    private fun loadMockData() {
        val mocks = listOf(
            Asset(
                id = "1",
                storyId = "story_1",
                title = "Journey Through Woods",
                // 使用网络图片
                thumbnailUrl = "https://img.freepik.com/free-photo/forest-landscape-with-sun-rays_23-2147956965.jpg",
                status = "completed"
            ),
            Asset(
                id = "2",
                storyId = "story_2",
                title = "Sunset at the Summit",
                thumbnailUrl = "https://img.freepik.com/free-photo/beautiful-sunset-mountain-landscape_23-2147956966.jpg",
                status = "completed"
            ),
            Asset(
                id = "3",
                storyId = "story_3",
                title = "Morning Fog",
                thumbnailUrl = "https://img.freepik.com/free-photo/misty-morning-mountains_23-2147956967.jpg",
                status = "generating"
            ),
            Asset(
                id = "4",
                storyId = "story_4",
                title = "Ocean View",
                thumbnailUrl = "https://img.freepik.com/free-photo/beautiful-tropical-beach-sea-ocean-with-white-cloud-blue-sky_74190-7459.jpg",
                status = "completed"
            )
        )
        _assetsList.value = mocks
    }

    // 2. 网络请求加载真实数据 (后续对接用)
    fun loadAssetsFromRepository() {
        safeLaunch {
            try {
                _assetsList.value =assetRepository.fetchAllRemoteAssets()
            } catch (e: Exception) {
                Log.e("AssetsViewModel", "Error loading assets: ${e.message}")
                loadMockData()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // 本地搜索过滤逻辑
        if (query.isEmpty()) {
            loadAssetsFromRepository() // 恢复所有数据
        } else {
            val currentList = _assetsList.value
            _assetsList.value = currentList.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }
    fun refreshQuery() {
        _searchQuery.value = ""
    }
}