package com.shiyao.ai_story.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ExoPlayerHelper - 稳定版视频播放封装工具
 * 播放、暂停、跳转、速度控制
 * 播放状态流、进度流、总时长流，方便UI绑定
 * 无实验性API，稳定可用
 */
@Suppress("unused")
object ExoPlayerHelper {

    private const val TAG = "ExoPlayerHelper"
    private var exoPlayer: ExoPlayer? = null

    // 播放状态流
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    // 播放进度流
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    // 总时长流
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    /**
     * 初始化 ExoPlayer
     */
    @OptIn(UnstableApi::class)
    fun initializePlayer(context: Context): ExoPlayer {
        val player = ExoPlayer.Builder(context)
            .build()

        // 监听播放状态变化
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        Log.d(TAG, "Player ready")
                        _duration.value = player.duration
                    }

                    Player.STATE_BUFFERING -> Log.d(TAG, "Player buffering")
                    Player.STATE_ENDED -> Log.d(TAG, "Player ended")
                    Player.STATE_IDLE -> Log.d(TAG, "Player idle")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPositionDiscontinuity(reason: Int) {
                _currentPosition.value = player.currentPosition
            }
        })

        exoPlayer = player
        return player
    }

    /**
     * 获取 ExoPlayer 实例，若未初始化则先初始化
     */
    fun getPlayer(context: Context): ExoPlayer {
        return exoPlayer ?: initializePlayer(context)
    }

    /**
     * 释放播放器资源
     */
    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
        Log.d(TAG, "Player released")
    }

    /**
     * 准备播放视频 URI
     */
    fun preparePlayer(context: Context, uri: Uri, autoPlay: Boolean = false) {
        val player = getPlayer(context)
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        if (autoPlay) player.play()
    }

    /**
     * 准备播放本地文件路径
     */
    fun preparePlayer(context: Context, filePath: String, autoPlay: Boolean = false) {
        preparePlayer(context, filePath.toUri(), autoPlay)
    }

    /**
     * 播放 / 暂停切换
     */
    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    /**
     * 播放
     */
    fun play() = exoPlayer?.play()

    /**
     * 暂停
     */
    fun pause() = exoPlayer?.pause()

    /**
     * 跳转到指定位置
     */
    fun seekTo(position: Long) = exoPlayer?.seekTo(position)

    /**
     * 设置播放速度
     */
    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    /**
     * 获取当前播放速度
     */
    fun getPlaybackSpeed(): Float = exoPlayer?.playbackParameters?.speed ?: 1.0f

    /**
     * 设置音量 0f~1f
     */
    fun setVolume(volume: Float) {
        exoPlayer?.volume = volume.coerceIn(0f, 1f)
    }

    /**
     * 获取当前音量
     */
    fun getVolume(): Float = exoPlayer?.volume ?: 1f

    /**
     * 静音（volume = 0f）
     */
    fun mute() {
        exoPlayer?.volume = 0f
    }

    /**
     * 取消静音（恢复为 1f）
     */
    fun unmute() {
        exoPlayer?.volume = 1f
    }
}
