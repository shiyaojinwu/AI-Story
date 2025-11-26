package com.shiyao.ai_story.navigation

enum class AppRoute(val route: String) {
    CREATE("create"),
    ASSETS("assets"),
    PREVIEW("preview/{assetName}"),
    GENERATE_STORY("generate_shot/{storyId}");

    companion object {
        fun generateShotRoute(storyId: String): String = "generate_shot/$storyId"
        fun previewRoute(assetName: String): String = "preview/$assetName"
    }
}
