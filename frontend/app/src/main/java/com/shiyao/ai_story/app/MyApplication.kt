package com.shiyao.ai_story.app

import android.app.Application
import com.shiyao.ai_story.model.dao.AppDatabase

class MyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}