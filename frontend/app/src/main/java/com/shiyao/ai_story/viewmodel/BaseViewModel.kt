package com.shiyao.ai_story.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiyao.ai_story.exception.AppException
import com.shiyao.ai_story.exception.DatabaseException
import com.shiyao.ai_story.exception.NetworkException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel基类
 * 封装通用的协程处理和异常处理逻辑
 */
abstract class BaseViewModel : ViewModel() {
    companion object {
        private const val TAG = "BaseViewModel"
    }

    // 全局异常处理器
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    /**
     * 安全地启动协程（自动处理异常）
     */
    protected fun safeLaunch(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            block()
        }
    }

    /**
     * 安全地启动协程并返回Job（用于轮询等需要取消的场景）
     */
    protected fun safeLaunchJob(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            block()
        }
    }

    /**
     * 处理异常
     */
    private fun handleException(throwable: Throwable) {
        when (throwable) {
            is NetworkException -> {
                Log.e(TAG, "Network Error: ${throwable.message}")
                onNetworkError(throwable)
            }
            is DatabaseException -> {
                Log.e(TAG, "Database Error: ${throwable.message}")
                onDatabaseError(throwable)
            }
            is AppException -> {
                Log.e(TAG, "App Error: ${throwable.message}")
                onAppError(throwable)
            }
            else -> {
                Log.e(TAG, "Unknown Error: ${throwable.message}")
                onUnknownError(throwable)
            }
        }
    }

    /**
     * 网络异常回调
     */
    protected open fun onNetworkError(e: NetworkException) {}

    /**
     * 数据库异常回调
     */
    protected open fun onDatabaseError(e: DatabaseException) {}

    /**
     * 应用异常回调
     */
    protected open fun onAppError(e: AppException) {}

    /**
     * 未知异常回调
     */
    protected open fun onUnknownError(e: Throwable) {}
}
