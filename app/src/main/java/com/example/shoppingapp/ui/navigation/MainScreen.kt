package com.example.shoppingapp.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.ui.cart.CartScreen
import com.example.shoppingapp.ui.cart.CartViewModel
import com.example.shoppingapp.ui.home.HomeScreen
import com.example.shoppingapp.ui.home.HomeViewModel
import com.example.shoppingapp.ui.order.OrderListScreen
import com.example.shoppingapp.ui.order.OrderViewModel
import com.example.shoppingapp.ui.profile.ProfileScreen
import com.example.shoppingapp.ui.search.SearchScreen
import com.example.shoppingapp.ui.theme.NavigationType
import com.example.shoppingapp.ui.theme.navigationType

@Composable
fun MainScreen(
    onProductClick: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    onLogout: () -> Unit,
    onAdminClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val cartViewModel: CartViewModel = hiltViewModel()
    val cartCount by cartViewModel.cartCount.collectAsState()

    // 使用 BoxWithConstraints 检测屏幕宽度，决定导航方式
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val navType = navigationType()

        if (navType == NavigationType.NavigationRail) {
            // ========== 平板模式：NavigationRail + 内容区 ==========
            Row(modifier = Modifier.fillMaxSize()) {
                // 侧边导航栏
                NavigationRail(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Text(
                            text = "黑胶",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                ) {
                    BottomNavItem.items.forEachIndexed { index, item ->
                        NavigationRailItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                if (item is BottomNavItem.Cart && cartCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge { Text("$cartCount") }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selectedTab == index) item.selectedIcon
                                                else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (selectedTab == index) item.selectedIcon
                                            else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            },
                            label = { Text(item.title) },
                            alwaysShowLabel = true
                        )
                    }
                }

                // 内容区
                MainContent(
                    selectedTab = selectedTab,
                    cartCount = cartCount,
                    onProductClick = onProductClick,
                    onCheckoutClick = onCheckoutClick,
                    onOrderClick = onOrderClick,
                    onLogout = onLogout,
                    onAdminClick = onAdminClick,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // ========== 手机模式：底部导航栏 ==========
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        BottomNavItem.items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = {
                                    if (item is BottomNavItem.Cart && cartCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge { Text("$cartCount") }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (selectedTab == index) item.selectedIcon
                                                    else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (selectedTab == index) item.selectedIcon
                                                else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    }
                                },
                                label = { Text(item.title) }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                MainContent(
                    selectedTab = selectedTab,
                    cartCount = cartCount,
                    onProductClick = onProductClick,
                    onCheckoutClick = onCheckoutClick,
                    onOrderClick = onOrderClick,
                    onLogout = onLogout,
                    onAdminClick = onAdminClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    selectedTab: Int,
    cartCount: Int,
    onProductClick: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    onOrderClick: (String) -> Unit,
    onLogout: () -> Unit,
    onAdminClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 内容区域 — 使用 contentMaxWidth 限制最大宽度
    androidx.compose.foundation.layout.Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (selectedTab) {
            0 -> HomeScreen(
                onProductClick = onProductClick,
                modifier = Modifier.fillMaxSize()
            )
            1 -> SearchScreen(
                onProductClick = onProductClick,
                modifier = Modifier.fillMaxSize()
            )
            2 -> CartScreen(
                onCheckoutClick = onCheckoutClick,
                onProductClick = onProductClick,
                modifier = Modifier.fillMaxSize()
            )
            3 -> OrderListScreen(
                onOrderClick = onOrderClick,
                modifier = Modifier.fillMaxSize()
            )
            4 -> ProfileScreen(
                onLogout = onLogout,
                onAdminClick = onAdminClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
