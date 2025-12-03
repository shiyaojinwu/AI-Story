package com.shiyao.ai_story.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络客户端配置
 * 单例模式，统一管理OkHttp和Retrofit实例
 */
object NetworkClient {
    /**
     * 服务端URL，后续可通过BuildConfig配置不同环境
     */
    private const val BASE_URL = "http://129.204.21.232:9000"

    /**
     * OkHttp客户端实例
     */
    val okHttpClient: OkHttpClient by lazy {
        // 创建日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor()
        //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // 添加日志拦截器
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
            .readTimeout(120, TimeUnit.SECONDS) // 读取超时
            .writeTimeout(120, TimeUnit.SECONDS) // 写入超时
            .build()
    }

    /**
     * Retrofit实例
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 设置基础URL
            .client(okHttpClient) // 关联OkHttp客户端
            .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
            .build()
    }

    /**
     * API服务实例
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
