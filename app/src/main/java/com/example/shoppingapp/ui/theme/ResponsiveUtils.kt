package com.example.shoppingapp.ui.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 窗口尺寸分类
 */
enum class WindowSizeClass {
    /** < 600dp — 手机竖屏 */
    Compact,
    /** 600dp ~ 840dp — 折叠屏/小平板 */
    Medium,
    /** >= 840dp — 平板/横屏 */
    Expanded
}

/** 导航模式 */
enum class NavigationType {
    /** 底部导航栏 — 手机 */
    BottomNavigation,
    /** 侧边导航栏 + 抽屉 — 平板 */
    NavigationRail
}

/** 详情页布局模式 */
enum class DetailLayoutType {
    /** 竖排 — 图片在上，信息在下 */
    Vertical,
    /** 横排 — 图片在左，信息在右 */
    Horizontal
}

/**
 * 根据窗口宽度获取尺寸分类
 */
fun BoxWithConstraintsScope.windowSizeClass(): WindowSizeClass {
    return when {
        maxWidth < 600.dp -> WindowSizeClass.Compact
        maxWidth < 840.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}

/**
 * 根据窗口宽度获取导航模式
 */
fun BoxWithConstraintsScope.navigationType(): NavigationType {
    return if (maxWidth >= 840.dp) NavigationType.NavigationRail
    else NavigationType.BottomNavigation
}

/**
 * 根据窗口宽度获取详情页布局
 */
fun BoxWithConstraintsScope.detailLayoutType(): DetailLayoutType {
    return if (maxWidth >= 840.dp) DetailLayoutType.Horizontal
    else DetailLayoutType.Vertical
}

/**
 * 商品网格列数（自适应）
 */
fun BoxWithConstraintsScope.gridColumns(): Int {
    return when {
        maxWidth >= 840.dp -> 4
        maxWidth >= 600.dp -> 3
        else -> 2
    }
}

/**
 * 商品网格最小卡片宽度（用于 GridCells.Adaptive）
 */
fun BoxWithConstraintsScope.gridMinCardWidth(): Dp {
    return when {
        maxWidth >= 840.dp -> 180.dp
        maxWidth >= 600.dp -> 180.dp
        else -> 160.dp
    }
}

/**
 * 内容区域最大宽度（大屏防"拉面条"）
 */
fun BoxWithConstraintsScope.contentMaxWidth(): Dp {
    return when {
        maxWidth >= 840.dp -> 1200.dp
        maxWidth >= 600.dp -> 840.dp
        else -> Dp.Unspecified
    }
}

/**
 * 水平内边距（大屏稍大）
 */
fun BoxWithConstraintsScope.contentHorizontalPadding(): Dp {
    return when {
        maxWidth >= 840.dp -> 48.dp
        maxWidth >= 600.dp -> 32.dp
        else -> 16.dp
    }
}

/**
 * 响应式宽度的便捷 Composable
 * 在需要响应式布局的 Composable 中直接使用 BoxWithConstraints 替代普通 Box/Column
 *
 * 用法：
 * ```kotlin
 * ResponsiveBox { // this: BoxWithConstraintsScope
 *     when (windowSizeClass()) {
 *         WindowSizeClass.Compact -> // 手机布局
 *         WindowSizeClass.Expanded -> // 平板布局
 *     }
 * }
 * ```
 */
@Composable
fun ResponsiveBox(
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    BoxWithConstraints(content = content)
}
