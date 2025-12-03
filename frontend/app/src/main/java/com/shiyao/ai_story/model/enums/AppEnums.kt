package com.shiyao.ai_story.model.enums

enum class Style { MOVIE, ANIMATION, REALISTIC }

enum class Status(val value: String) {
    GENERATING("generating"),  // 正在生成
    COMPLETED("completed"),    // 已完成
    FAILED("failed");          // 生成失败

    companion object {
        fun from(value: String?): Status {
            return when (value?.lowercase()) {
                "generating" -> GENERATING
                "completed" -> COMPLETED
                "failed" -> FAILED
                else -> GENERATING // 默认状态
            }
        }
    }
}