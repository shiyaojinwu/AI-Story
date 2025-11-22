package com.shiyao.ai_story.model.network

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)
