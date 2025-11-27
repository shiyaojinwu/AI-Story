package com.shiyao.ai_story.viewmodel

/**
 * UI状态密封类
 * 用于统一管理UI的不同状态
 */
sealed class
UIState<out T> {
    // 初始状态
    object Initial : UIState<Nothing>()

    // 加载中状态
    object Loading : UIState<Nothing>()

    // 成功状态，包含数据
    data class Success<out T>(val data: T) : UIState<T>()

    // 错误状态，包含异常信息f
    data class Error(val exception: Throwable, val message: String? = null) : UIState<Nothing>()

    // 空数据状态
    object Empty : UIState<Nothing>()

    // 检查是否是加载状态
    val isLoading: Boolean get() = this is Loading

    // 检查是否是成功状态
    val isSuccess: Boolean get() = this is Success<*>

    // 检查是否是错误状态
    val isError: Boolean get() = this is Error

    // 获取成功状态的数据
    fun getOrNull(): T? = (this as? Success<T>)?.data
}
