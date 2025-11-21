package com.shiyao.ai_story.navigation

enum class AppRoute(val route: String) {
    CREATE("create"),
    ASSETS("assets");

    // 带参路由
    fun withArg(arg: String) = "$route/$arg"
}