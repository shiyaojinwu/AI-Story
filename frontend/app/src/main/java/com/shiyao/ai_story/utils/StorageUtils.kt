package com.shiyao.ai_story.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.shiyao.ai_story.model.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * StorageUtils - 文件存储工具类
 * 封装 Scoped Storage 和传统存储操作，支持视频、资产文件等
 */
@Suppress("unused")
object StorageUtils {

    private const val APP_DIR_NAME = "AI-Story"
    private const val VIDEO_DIR_NAME = "videos"

    /**
     * 获取应用的外部文件目录（兼容 Android Q+ 和传统存储）
     */
    fun getAppExternalFilesDir(context: Context, type: String? = null): File? {
        return context.getExternalFilesDir(type) ?: Environment.getExternalStorageDirectory()
    }

    /**
     * 获取应用的视频存储目录
     */
    fun getVideoStorageDir(context: Context): File? {
        val dir =
            File(getAppExternalFilesDir(context, Environment.DIRECTORY_MOVIES), VIDEO_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /**
     * 在 MediaStore 中保存视频文件 (Android Q+)
     */
    fun saveVideoToMediaStore(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        mimeType: String = "video/mp4",
        description: String = "AI Story Generated Video"
    ): Uri? {
        val resolver = context.contentResolver
        var uri: Uri? = null

        return try {
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Video.Media.MIME_TYPE, mimeType)
                put(MediaStore.Video.Media.DESCRIPTION, description)
                put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/$APP_DIR_NAME")
            }

            uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)

            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    inputStream.copyTo(out)
                }
            }

            uri
        } catch (e: Exception) {
            e.printStackTrace()
            // 出现异常时删除已创建的 MediaStore 条目
            uri?.let { resolver.delete(it, null, null) }
            null
        }
    }

    /**
     * 下载网络视频并保存到 MediaStore
     */
    suspend fun saveNetworkVideoToMediaStore(
        context: Context,
        url: String,
        fileName: String,
        mimeType: String = "video/mp4",
        description: String = "AI Story Network Video"
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            val request = okhttp3.Request.Builder().url(url).build()
            NetworkClient.okHttpClient.newCall(request).execute().use { response ->
                val body = response.body
                body.byteStream().use { inputStream ->
                    saveVideoToMediaStore(context, inputStream, fileName, mimeType, description)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 保存文件到应用内部存储
     */
    fun saveFileToInternalStorage(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        directory: String = VIDEO_DIR_NAME
    ): File? {
        val dir = File(context.filesDir, directory).apply { if (!exists()) mkdirs() }
        val file = File(dir, fileName)
        return try {
            file.outputStream().use { inputStream.copyTo(it) }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            if (file.exists()) file.delete()
            null
        }
    }

    /**
     * 从内部存储读取文件
     */
    fun readFileFromInternalStorage(
        context: Context,
        fileName: String,
        directory: String = VIDEO_DIR_NAME
    ): File? {
        val file = File(File(context.filesDir, directory), fileName)
        return file.takeIf { it.exists() }
    }

    /**
     * 获取文件大小 (字节)
     */
    fun getFileSize(file: File?): Long = file?.length() ?: 0L

    /**
     * 通过 Uri 获取文件大小
     */
    fun getFileSizeFromUri(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    /**
     * 获取文件 MIME 类型
     */
    fun getMimeType(file: File?): String {
        return when (file?.extension?.lowercase()) {
            "mp4" -> "video/mp4"
            "mp3" -> "audio/mp3"
            "wav" -> "audio/wav"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"
        }
    }
}
