# 🎵 黑胶唱片商店 - VinylShop

基于 Android Jetpack Compose 的黑胶唱片购物应用

[![Release](https://img.shields.io/badge/release-v1.0.0-blue.svg)](https://github.com/lky-hjh/VinylShop/releases/tag/v1.0.0-release)
[![Android](https://img.shields.io/badge/SDK-34+-green.svg)](https://developer.android.com/studio/releases/platforms)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org/)

---

## ✨ 功能特性

- **首页** — Hero Banner + 流派筛选 + 精选推荐 + 新品上市
- **搜索** — 关键词 + 流派筛选
- **购物车** — 数量增减、删除、结算
- **订单** — 订单列表 + 详情
- **个人中心** — 登录注册、个人信息管理、第三方登录（微信/QQ）
- **管理员后台** — 商品 CRUD、用户管理、权限控制
- **响应式布局** — 手机/平板/横屏自适应

### 响应式设计

| 设备 | 导航栏 | 商品网格 | 详情页 |
|------|--------|----------|--------|
| 手机竖屏 | 底部导航 | 2列 | 单栏 |
| 平板/横屏 | 侧边导航 | 3~4列 | 双栏 |

---

## 🛠️ 技术栈

| 技术 | 用途 |
|------|------|
| **Kotlin** | 开发语言 |
| **Jetpack Compose + Material 3** | UI 框架 |
| **MVVM + Repository Pattern** | 架构模式 |
| **Hilt** | 依赖注入 |
| **Compose Navigation** | 页面导航 |
| **Room (SQLite)** | 本地数据库 |
| **Coil** | 图片加载 |
| **DataStore Preferences** | 用户状态持久化 |
| **Gradle (AGP 8.x)** | 构建工具 |

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Android Studio**: Hedgehog (2023.1.1) 或更新版本
- **Android SDK**: API Level 34+
- **内存**: 建议 8GB RAM 以上（用于 Gradle 构建）

### 第一步：克隆项目

```bash
# 使用 HTTPS 克隆
git clone https://github.com/lky-hjh/VinylShop.git

# 或使用 SSH（如果已配置 SSH Key）
git clone git@github.com:lky-hjh/VinylShop.git

# 进入项目目录
cd VinylShop
```

### 第二步：使用 Android Studio 打开项目

1. 打开 Android Studio
2. 选择 **File → Open**
3. 选择 `VinylShop` 文件夹
4. 点击 **OK**

### 第三步：同步 Gradle 依赖

首次打开时，Android Studio 会自动提示同步 Gradle：

1. 点击 **Sync Now**（或提示框中的按钮）
2. 等待依赖下载完成（首次可能需要 5-10 分钟）

> 💡 **提示：** 如果自动同步失败，可以手动执行：
> ```bash
> ./gradlew --refresh-dependencies
> ```

### 第四步：构建 Debug 版本

#### 方式一：通过 Android Studio

1. 选择菜单 **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. 构建完成后，点击右下角通知中的 **locate**
3. APK 路径：`app/build/outputs/apk/debug/app-debug.apk`

#### 方式二：通过命令行

```bash
# Windows (Git Bash / PowerShell)
./gradlew assembleDebug

# macOS / Linux
./gradlew assembleDebug

# APK 输出路径
app/build/outputs/apk/debug/app-debug.apk
```

### 第五步：安装到设备

#### 使用 USB 连接真机

```bash
# 确保 USB 调试已开启
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 使用模拟器

1. 在 Android Studio 中创建或启动模拟器（建议 API 34+）
2. 直接点击运行按钮 ▶️ 即可自动安装并启动

---

## 🎯 Release 构建（可选）

如果需要生成签名后的 release APK：

### 创建 Keystore（如果没有）

```bash
# 在 app 目录下生成 keystore 文件
keytool -genkey -v -keystore app/release.jks \
  -alias VinylShop \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass 123456 -keypass 123456
```

### 构建 Release APK

```bash
# 仅当 release.jks 存在时会使用签名配置
./gradlew assembleRelease

# APK 输出路径
app/build/outputs/apk/release/app-release.apk
```

> ⚠️ **注意：** 项目已配置自动检测 keystore 是否存在。如果 `release.jks` 不存在，release 构建会使用 debug 签名。

---

## 📱 测试账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 商品管理、用户管理 |
| 普通用户 | user | user123 | 购物车、下单 |

### 第三方登录

- **微信/QQ 登录**：当前为模拟实现（无需真实资质）
- 点击后会自动创建 mock 用户并登录

---

## 🏗️ 项目结构

```
app/src/main/java/com/example/shoppingapp/
├── data/           # 数据层
│   ├── local/      #   Room 数据库
│   │   ├── AppDatabase.kt        # 数据库定义
│   │   ├── dao/                   # 数据访问对象
│   │   └── entity/                # 实体类
│   ├── repository/ #   Repository 模式
│   └── seed/       #   种子数据（初始商品、用户）
├── di/             # Hilt 依赖注入模块
├── domain/         # 领域层
│   └── model/      # 数据模型
├── ui/             # UI 层
│   ├── admin/      #   管理员界面
│   ├── cart/       #   购物车
│   ├── checkout/   #   结算页面
│   ├── components/ #   通用组件
│   ├── detail/     #   商品详情
│   ├── home/       #   首页
│   ├── login/      #   登录注册
│   ├── navigation/ #   导航图
│   ├── order/      #   订单
│   ├── profile/    #   个人中心
│   ├── search/     #   搜索
│   └── theme/      #   主题 + 响应式工具
└── util/           # 工具类
```

---

## ❓ 常见问题

### Q1: Gradle 同步失败？

**问题现象：**
```
Failed to resolve: com.google.dagger:hilt-android:xxx
```

**解决方案：**
1. 检查网络连接（确保能访问 Google Maven 仓库）
2. 如果在国内，建议配置代理或镜像源：
   ```kotlin
   // settings.gradle.kts 中添加阿里云镜像
   maven("https://maven.aliyun.com/repository/google")
   maven("https://maven.aliyun.com/repository/public")
   ```
3. 清除缓存后重新同步：
   ```bash
   ./gradlew clean
   ./gradlew --refresh-dependencies
   ```

### Q2: compileSdk 版本不匹配？

**问题现象：**
```
SDK location not found. Define location with an ANDROID_SDK_ROOT environment variable.
```

**解决方案：**
1. 打开 Android Studio → Settings → SDK Manager
2. 安装 **Android SDK Platform 34**
3. 设置 `ANDROID_SDK_HOME` 环境变量到 SDK 安装目录

### Q3: JDK 版本问题？

**问题现象：**
```
Unsupported class file major version 65
```

**解决方案：**
- 项目需要 **JDK 17+**
- 在 Android Studio 中设置：File → Project Structure → SDK Location → JDK location
- 或设置环境变量：
  ```bash
  set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
  ```

### Q4: Hilt 注入失败？

**问题现象：**
```
[Dagger/MissingBinding] xxx cannot be provided without an @Provides method.
```

**解决方案：**
1. 清除构建缓存：Build → Clean Project
2. 重新构建：Build → Rebuild Project
3. 如果仍失败，删除 `.gradle` 和 `build` 文件夹后重新同步

### Q5: Release 签名失败？

**问题现象：**
```
Keystore file not found for signing config 'release'
```

**解决方案：**
- 这是正常的！项目已配置为当 `release.jks` 不存在时使用 debug 签名
- 如果确实需要正式签名，请参考上方"Release 构建"章节生成 keystore

---

## 📋 Android 考核点覆盖

本项目涵盖以下 Android 开发知识点：

| 类别 | 知识点 | 实现位置 |
|------|--------|----------|
| **UI 开发** | Jetpack Compose | 全项目使用 |
| **Material Design 3** | 主题系统、组件库 | ui/theme/ |
| **响应式布局** | 手机/平板适配 | theme/Responsive.kt |
| **架构模式** | MVVM | 各 Screen + ViewModel |
| **数据持久化** | Room 数据库 | data/local/ |
| **依赖注入** | Hilt | di/ 模块 |
| **导航** | Compose Navigation | ui/navigation/ |
| **异步编程** | Kotlin Coroutines | ViewModel + Repository |
| **图片加载** | Coil | 详情页/首页 |
| **本地存储** | DataStore | 登录状态持久化 |
| **传统布局** | XML + Fragment | LegacyDemoActivity.kt |
| **生命周期** | Lifecycle Awareness | MainActivity.kt |
| **安全** | 密码哈希 | UserRepository.kt |

---

## 🤝 协作开发

### Fork 并贡献

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feat/your-feature`
3. 提交更改：`git commit -m "feat: 添加某功能"`
4. 推送到你的 fork：`git push origin feat/your-feature`
5. 提交 Pull Request

### Git 工作流建议

```bash
# 从 main 创建新功能分支
git checkout -b feat/my-feature origin/main

# 开发过程中定期同步主分支最新代码
git fetch origin
git rebase origin/main

# 推送前整理提交历史（可选）
git rebase -i origin/main

# 安全推送（使用 --force-with-lease）
git push --force-with-lease origin feat/my-feature
```

---

## 📄 许可证

本项目采用 MIT License - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 👨‍💻 作者

**lky-hjh** - [GitHub](https://github.com/lky-hjh)

如有问题欢迎提 Issue 或 PR！

---

## 📝 更新日志

### v1.0.0-release (2026-05)

- ✅ 初始版本发布
- ✅ 完整的购物流程（浏览→加购→下单→订单）
- ✅ 用户认证系统（登录/注册/第三方登录）
- ✅ 管理员后台（商品/用户管理）
- ✅ 响应式布局适配
- ✅ 修复用户不存在 Bug
