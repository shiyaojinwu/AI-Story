package com.shiyao.ai_story.navigation

enum class AppRoute(val route: String) {
    CREATE("create"),
    ASSETS("assets"),
    PREVIEW("preview/{assetName}"),
    GENERATE_STORY("generate_shot/{storyId}"),
    // ⚠️ 新增：分镜详情页路由
    SHOT_DETAIL("shot_detail");

    companion object {
        fun generateShotRoute(storyId: String): String = "generate_shot/$storyId"
        fun previewRoute(assetName: String): String = "preview/$assetName"
        // ⚠️ 新增：分镜详情页路由函数
        fun shotDetailRoute(): String = SHOT_DETAIL.route
    }
}
