package com.shiyao.ai_story.model.repository

import com.shiyao.ai_story.exception.DatabaseException
import com.shiyao.ai_story.exception.NetworkException
import com.shiyao.ai_story.model.network.ApiResponse
import retrofit2.Response

/**
 * Repository基类：统一处理网络层与业务层错误
 */
abstract class BaseRepository {

    /**
     * 处理网络响应（支持 Response<ApiResponse<T>>）
     */
    protected fun <T> handleResponse(
        response: Response<ApiResponse<T>>
    ): T {
        if (!response.isSuccessful) {
            throw NetworkException(
                "HTTP ERROR: ${response.code()} - ${response.message()}"
            )
        }
        val body = response.body()
            ?: throw NetworkException("HTTP BODY IS NULL")

        // API文档：code: 0 表示成功
        if (body.code != 0) {
            throw NetworkException(
                "API ERROR: ${body.code} - ${body.message}"
            )
        }
        return body.data
            ?: throw NetworkException("API DATA IS NULL")
    }

    /**
     * 处理数据库操作结果
     */
    protected fun <T> handleDatabaseResult(result: T?): T {
        return result
            ?: throw DatabaseException("Database operation returned null result")
    }
}
