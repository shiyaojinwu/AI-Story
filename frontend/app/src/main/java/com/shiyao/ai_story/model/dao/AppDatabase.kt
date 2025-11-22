package com.shiyao.ai_story.model.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shiyao.ai_story.model.entity.Asset
import com.shiyao.ai_story.model.entity.Shot
import com.shiyao.ai_story.model.entity.Story
import com.shiyao.story_creat.model.dao.AssetDao


/**
 * 应用数据库类
 */
@Database(
    entities = [Story::class, Shot::class, Asset::class],
    version = 1,
    exportSchema = false
)
@Suppress("unused")
abstract class AppDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun shotDao(): ShotDao
    abstract fun assetDao(): AssetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库实例
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_story_db"
                )
                    .fallbackToDestructiveMigration() // 数据库版本升级时销毁旧数据
                    // .addMigrations(MIGRATION_1_2) // 注册 Migration迁移策略，表结构变更才要
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         *  Migration迁移策略
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 结构变更并迁移
                db.execSQL("ALTER TABLE stories ADD COLUMN new_column TEXT")
            }
        }
    }
}
