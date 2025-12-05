# AI-Story 项目文档

## 1. 项目概述
AI-Story 是一个基于 Android 平台的 AI 故事创作应用，采用现代化的 Jetpack Compose 技术栈构建，提供分镜式故事创作与管理功能。

### 核心功能
- 📝 故事创建
- 🎬 分镜设计与编辑
- 📦 视频资源管理
- 👁️ 预览与导出
- 📊 数据持久化

## 2. 技术架构
采用分层架构设计，遵循现代 Android 应用开发最佳实践：

```
┌───────────────────┐
│   Presentation    │  (UI层 - Jetpack Compose)
├───────────────────┤
│    ViewModel      │  (业务逻辑层 - MVVM)
├───────────────────┤
│    Repository     │  (数据仓库层 - 数据抽象)
├───────────────────┤
│     Data Layer    │  (数据层 - Room + API)
└───────────────────┘
```

### 技术栈
| 技术领域         | 技术选择                     |
|------------------|------------------------------|
| 界面框架         | Jetpack Compose              |
| 架构模式         | MVVM (Model-View-ViewModel)  |
| 数据持久化       | Room Database                |
| 导航组件         | Jetpack Navigation Compose   |
| 依赖注入         | ViewModel Factory            |
| 语言             | Kotlin                       |

## 3. 项目结构

### 核心包结构
```
com.shiyao.ai_story
├── app/                  # 应用初始化
│   └── MyApplication.kt  # 全局应用类
├── components/           # 通用组件库
│   ├── BottomNavBar.kt   # 底部导航栏
│   ├── CommonButton.kt   # 通用按钮
│   ├── CommonCard.kt     # 通用卡片
│   └── ...               # 其他通用组件
├── exception/            # 自定义异常
│   ├── AppException.kt   # 应用异常
│   └── ...
├── model/                # 数据模型层
│   ├── dao/              # Room DAO 接口
│   ├── entity/           # 数据库实体
│   ├── repository/       # 数据仓库
│   ├── enums/            # 枚举类型
│   └── ...
├── navigation/           # 导航管理
│   ├── AppRoute.kt       # 路由定义
│   └── AppNavigation.kt  # 导航图
├── screens/              # 应用界面
│   ├── CreateScreen.kt   # 创建页
│   ├── AssetsScreen.kt   # 素材页
│   ├── ShotScreen.kt     # 分镜页
│   └── ...
├── utils/                # 工具类
└── viewmodel/            # ViewModel 层
```

## 4. 关键模块设计

### 4.1 数据层设计
**AppDatabase.kt** (app/src/main/java/com/shiyao/ai_story/model/dao/AppDatabase.kt)
- 使用 Room Database 实现数据持久化
- 支持实体：Story、Shot、Asset
- 版本管理与迁移支持

```kotlin
@Database(
    entities = [Story::class, Shot::class, Asset::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun shotDao(): ShotDao
    abstract fun assetDao(): AssetDao
    // ... 单例实现与迁移策略
}
```

### 4.2 导航系统
**AppNavigation.kt** (app/src/main/java/com/shiyao/ai_story/navigation/AppNavigation.kt)
- 采用 Jetpack Navigation Compose 实现单 Activity 架构
- 统一管理应用路由与参数传递
- 支持 ViewModel 共享与注入

```kotlin
@Composable
fun AppNavigation(navController: NavHostController) {
    val database = (applicationContext as MyApplication).database
    // ... 依赖初始化
    NavHost(navController, startDestination = AppRoute.CREATE.route) {
        composable(AppRoute.CREATE.route) { /* 创建页 */ }
        composable(AppRoute.ASSETS.route) { /* 素材页 */ }
        // ... 其他路由
    }
}
```

### 4.3 组件系统
- 提供通用组件，实现 UI 一致性
- 支持自定义样式与状态管理
- 遵循单一职责原则，高复用性

## 5. 开发流程

### 5.1 代码风格
- 语言：Kotlin
- 命名：camelCase（变量/函数），PascalCase（类/接口）


### 5.2 构建流程
```bash
# 清理构建
./gradlew clean

# 构建 APK
./gradlew assembleDebug

# 运行应用
./gradlew installDebug
```

## 6. 核心功能说明

### 6.1 故事创作流程
1. **创建故事**：在 CreateScreen 输入故事基础信息
2. **生成分镜**：根据故事内容生成分镜
3. **编辑分镜**：在 ShotDetailScreen 编辑分镜
4. **添加素材**：从 AssetsScreen 查看素材
5. **预览**：在 PreviewScreen 查看视频，支持导出

### 6.2 数据关系
```
Story (1) → (N) Shot (1) → (N) Asset
```

## 7. 注意事项

1. **导航参数传递**：
- 敏感信息避免通过路由参数传递
- 使用 Uri.encode() 处理特殊字符

2. **组件设计**：
- 避免过度复杂的组合组件
