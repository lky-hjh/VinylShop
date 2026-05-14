# 黑胶唱片商店 - VinylShop

基于 Android Jetpack Compose 的黑胶唱片购物应用 🎵

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM + Repository Pattern
- **DI**: Hilt
- **导航**: Compose Navigation
- **数据库**: Room
- **图片加载**: Coil
- **构建**: Gradle (AGP 8.x)

## 功能特性

- 🏠 **首页** — Hero Banner + 流派筛选 + 精选推荐 + 新品上市
- 🔍 **搜索** — 关键词 + 流派筛选
- 🛒 **购物车** — 数量增减、删除、结算
- 📋 **订单** — 订单列表 + 详情
- 👤 **个人中心** — 登录注册、个人信息管理
- 📱 **响应式布局** — 手机/平板/横屏自适应
  - 手机竖屏：底部导航栏 + 2列网格
  - 平板/横屏：侧边导航栏 + 3~4列网格 + 双栏详情页

## 快速开始

### 环境要求
- JDK 17+
- Android Studio Hedgehog (2023.1.1) 或更新版本
- Android SDK 34+

### 构建运行

```bash
# 设置 JDK 17（Windows）
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot

# 调试构建
./gradlew assembleDebug

# 发布构建
./gradlew assembleRelease

# APK 输出路径
app/build/outputs/apk/debug/app-debug.apk
```

## 项目结构

```
app/src/main/java/com/example/shoppingapp/
├── data/           # 数据层 (Repository, DAO, Database)
├── di/             # Hilt 依赖注入模块
├── domain/         # 领域层 (Model, UseCase)
├── ui/             # UI 层
│   ├── cart/       # 购物车
│   ├── checkout/   # 结算
│   ├── components/ # 通用组件
│   ├── detail/     # 商品详情
│   ├── home/       # 首页
│   ├── login/      # 登录注册
│   ├── navigation/ # 导航
│   ├── order/      # 订单
│   ├── profile/    # 个人中心
│   ├── search/     # 搜索
│   └── theme/      # 主题 + 响应式工具
└── util/           # 工具类
```

## 授权

MIT License
