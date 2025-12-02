package com.shiyao.ai_story.navigation

import android.net.Uri

enum class AppRoute(val route: String) {
    CREATE("create"),
    ASSETS("assets"),
    PREVIEW("preview/{title}/{url}"),
    GENERATE_STORY("generate_shot/{storyId}"),
    SHOT_DETAIL("shot_detail/{shotId}");

    companion object {
        fun generateShotRoute(storyId: String): String = "generate_shot/$storyId"
        fun previewRoute(url: String, title: String): String {
            val encodedUrl = Uri.encode(url)
            val encodedTitle = Uri.encode(title)
            return "preview/$encodedTitle/$encodedUrl"
        }
        fun shotDetailRoute(shotId: String): String = "shot_detail/$shotId"
    }
}
