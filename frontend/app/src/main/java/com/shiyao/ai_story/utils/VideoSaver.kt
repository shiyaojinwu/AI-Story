package com.shiyao.ai_story.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object VideoSaver {

    suspend fun saveVideoToGallery(
        context: Context,
        videoUrl: String,
        fileName: String,
        onProgress: (Int) -> Unit
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("VideoSaver", "Start downloading with OkHttp: $videoUrl")
                // 创建客户端
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .build()
                //  构建请求
                val request = Request.Builder()
                    .url(videoUrl)
                    .build()
                //  执行请求
                val response = client.newCall(request).execute()
                val body = response.body ?: throw IOException("Response body is null")
                if (!response.isSuccessful) {
                    Log.e("VideoSaver", "Server error: ${response.code}")
                    return@withContext false
                }
                // 获取文件大小
                val totalSize = body.contentLength()
                Log.d("VideoSaver", "文件总大小: $totalSize")
                //  准备写入系统相册
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/StoryFlow")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext false
                // 开始搬运数据
                body.byteStream().use { input ->
                    resolver.openOutputStream(uri)?.use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesCopied: Long = 0
                        var bytes = input.read(buffer)
                        while (bytes >= 0) {
                            output.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            // 进度计算
                            if (totalSize > 0) {
                                val progress = (bytesCopied * 100L / totalSize).toInt()
                                onProgress(progress)
                            }
                            bytes = input.read(buffer)
                        }
                    }
                }
                Log.d("VideoSaver", "Download finished successfully")
                true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("VideoSaver", "Download failed: ${e.message}")
                false
            }
        }
    }
}