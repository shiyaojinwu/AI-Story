# AI-Story 应用

## 📁 项目结构

```
AI-Story/frontend/
├── app/                     # 主应用模块
│   ├── src/
│   │   └── main/
│   │       ├── java/com/shiyao/ai_story/
│   │       │   ├── components/        # 通用 UI 组件
│   │       │   │   ├── CommonButton.kt    # 通用按钮组件
│   │       │   │   ├── CommonCard.kt       # 通用卡片组件
│   │       │   │   └── CommonTextField.kt  # 通用文本输入组件
│   │       │   ├── model/             # 数据模型层
│   │       │   │   ├── dao/                # Room 数据库访问对象
│   │       │   │   │   ├── AppDatabase.kt     # 数据库实例
│   │       │   │   │   ├── AssetDao.kt        # 资源数据访问
│   │       │   │   │   ├── ShotDao.kt         # 镜头数据访问
│   │       │   │   │   ├── StoryboardDao.kt   # 分镜数据访问
│   │       │   │   │   └── StoryDao.kt        # 故事数据访问
│   │       │   │   └── entity/             # 数据库实体类
│   │       │   │       ├── Asset.kt         # 资源实体
│   │       │   │       ├── Shot.kt          # 镜头实体
│   │       │   │       ├── Story.kt         # 故事实体
│   │       │   │       └── Storyboard.kt    # 分镜实体
│   │       │   ├── navigation/        # 导航配置
│   │       │   │   ├── AppNavigation.kt     # 应用导航组件
│   │       │   │   └── AppRoute.kt          # 路由定义
│   │       │   ├── screens/           # 应用界面
│   │       │   │   ├── AssetsScreen.kt      # 资源管理界面
│   │       │   │   └── CreateScreen.kt      # 创建故事界面
│   │       │   ├── ui/theme/          # 主题配置
│   │       │   │   ├── Color.kt        # 颜色定义
│   │       │   │   ├── Theme.kt        # 主题样式
│   │       │   │   └── Type.kt         # 字体样式
│   │       │   ├── MainActivity.kt     # 主入口 Activity
│   │       │   └── TraditionalActivity.kt   # 传统 XML 界面示例
│   │       ├── res/                  # 资源文件
│   │       │   ├── drawable/         # 可绘制资源
│   │       │   ├── layout/           # 布局文件
│   │       │   ├── mipmap-*/         # 图标资源
│   │       │   ├── values/           # 字符串、颜色等
│   │       │   └── values-night/     # 夜间模式资源
│   │       └── AndroidManifest.xml   # 应用配置文件
│   └── build.gradle.kts              # 模块构建配置
├── gradle/                   # Gradle 配置
├── gradle.properties         # Gradle 全局属性
├── gradlew                   # Gradle 执行脚本
├── gradlew.bat               # Windows 执行脚本
├── settings.gradle.kts       # 项目设置
└── README.md                 # 项目说明文档
```

## 知识参考

+ [kotlin](https://developer.android.google.cn/kotlin/learn?hl=zh-cn)
+ [Jetpack Composes ](https://jetpackcompose.cn/docs/)
+ [Room 和 Flow ](https://developer.android.google.cn/codelabs/basic-android-kotlin-training-intro-room-flow?hl=zh_cn#0)
+ [viewModel](https://developer.android.google.cn/topic/libraries/architecture/viewmodel?hl=zh-cn)
+ [navigation](https://developer.android.google.cn/develop/ui/compose/navigation?hl=zh-cn)