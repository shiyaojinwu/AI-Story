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
│   │       │   │   │   └── StoryDao.kt        # 故事数据访问
│   │       │   │   ├── entity/             # 数据库实体类
│   │       │   │   │   ├── Asset.kt         # 资源实体
│   │       │   │   │   ├── Shot.kt          # 镜头实体
│   │       │   │   │   └── Story.kt         # 故事实体
│   │       │   │   ├── enums/             # 枚举类型
│   │       │   │   │   └── AppEnums.kt      # 应用枚举
│   │       │   │   ├── network/           # 网络请求层
│   │       │   │   │   ├── ApiResponse.kt    # 统一API响应
│   │       │   │   │   ├── ApiService.kt      # API服务接口
│   │       │   │   │   └── NetworkClient.kt   # 网络客户端配置
│   │       │   │   ├── repository/        # 数据仓库层
│   │       │   │   │   ├── AssetRepository.kt     # 资源仓库
│   │       │   │   │   ├── BaseRepository.kt      # 基础仓库
│   │       │   │   │   ├── ShotRepository.kt      # 镜头仓库
│   │       │   │   │   └── StoryRepository.kt     # 故事仓库
│   │       │   │   ├── request/           # 请求模型
│   │       │   │   │   ├── CreateStoryRequest.kt  # 创建故事请求
│   │       │   │   │   └── GenerateShotRequest.kt # 生成镜头请求
│   │       │   │   ├── response/          # 响应模型
│   │       │   │   │   ├── ShotResponses.kt       # 镜头响应
│   │       │   │   │   └── StoryResponses.kt      # 故事响应
│   │       │   │   └── ui/                # UI模型
│   │       │   │       └── ShotUI.kt          # 镜头UI模型
│   │       │   ├── navigation/        # 导航配置
│   │       │   │   ├── AppNavigation.kt     # 应用导航组件
│   │       │   │   └── AppRoute.kt          # 路由定义
│   │       │   ├── screens/           # 应用界面
│   │       │   │   ├── AssetsScreen.kt      # 资源管理界面
│   │       │   │   ├── CreateScreen.kt      # 创建故事界面
│   │       │   │   └── GenerateStoryScreen.kt     # 生成故事界面
│   │       │   ├── ui/theme/          # 主题配置
│   │       │   │   ├── Color.kt        # 颜色定义
│   │       │   │   ├── Theme.kt        # 主题样式
│   │       │   │   └── Type.kt         # 字体样式
│   │       │   ├── app/               # 应用初始化
│   │       │   │   └── MyApplication.kt      # 应用自定义Application
│   │       │   ├── exception/         # 异常处理
│   │       │   │   ├── AppException.kt        # 应用基础异常
│   │       │   │   ├── DatabaseException.kt   # 数据库异常
│   │       │   │   └── NetworkException.kt    # 网络异常
│   │       │   ├── viewmodel/         # 视图模型层
│   │       │   │   ├── BaseViewModel.kt       # 基础视图模型
│   │       │   │   ├── ShotViewModel.kt        # 镜头视图模型
│   │       │   │   ├── StoryViewModel.kt       # 故事视图模型
│   │       │   │   └── ViewModelFactory.kt     # 视图模型工厂
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