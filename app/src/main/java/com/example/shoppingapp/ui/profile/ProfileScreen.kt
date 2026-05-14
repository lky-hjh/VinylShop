package com.example.shoppingapp.ui.profile

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.shoppingapp.domain.model.User
import com.example.shoppingapp.ui.components.ErrorView
import com.example.shoppingapp.ui.components.LoadingIndicator
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.toDateString

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onAdminClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userState by viewModel.userState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout()
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (val state = userState) {
            is Resource.Loading -> LoadingIndicator()
            is Resource.Error -> {
                // 判断是否为登录态失效错误
                val isLoggedOut = state.message?.let {
                    it == "未登录" || it.contains("请重新登录")
                } == true

                if (isLoggedOut) {
                    // 登录态失效：显示"前往登录"按钮，点击直接跳转登录页
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "错误",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onLogout) {
                            Text("前往登录")
                        }
                    }
                } else {
                    // 普通错误（网络/服务器等）：显示重试按钮
                    ErrorView(
                        message = state.message ?: "",
                        onRetry = { viewModel.loadUser() }
                    )
                }
            }
            is Resource.Success -> {
                ProfileContent(
                    user = state.data,
                    onLogoutClick = { showLogoutDialog = true },
                    onOpenLegacyDemo = {
                        val intent = Intent(context, com.example.shoppingapp.LegacyDemoActivity::class.java)
                        context.startActivity(intent)
                    },
                    onAdminClick = onAdminClick
                )
            }
            null -> {}
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    onLogoutClick: () -> Unit,
    onOpenLegacyDemo: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    // Header
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        AsyncImage(
            model = user.avatar.ifEmpty {
                "https://ui-avatars.com/api/?name=${user.username}&background=1A1A2E&color=fff&size=128"
            },
            contentDescription = "头像",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 用户名 + 角色徽章
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.username,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (user.isAdmin)
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (user.isAdmin) "管理员" else "普通用户",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (user.isAdmin)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }

    HorizontalDivider()

    // Info cards
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "个人信息",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        InfoCard(
            icon = Icons.Filled.Email,
            label = "邮箱",
            value = user.email
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoCard(
            icon = Icons.Filled.Phone,
            label = "手机",
            value = user.phone.ifEmpty { "未设置" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoCard(
            icon = Icons.Filled.Person,
            label = "地址",
            value = user.address.ifEmpty { "未设置" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        InfoCard(
            icon = Icons.Filled.Receipt,
            label = "注册时间",
            value = user.createdAt.toDateString("yyyy-MM-dd")
        )
    }

    HorizontalDivider()

    // 管理员入口 — 仅管理员可见，体现权限管理的功能隔离
    if (user.isAdmin) {
        TextButton(
            onClick = onAdminClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Icon(
                Icons.Filled.AdminPanelSettings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "商品管理（管理员专属）",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        HorizontalDivider()
    }

    // Legacy Demo 入口（展示传统 Fragment + XML 用法）
    TextButton(
        onClick = onOpenLegacyDemo,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(
            Icons.Filled.Code,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "传统布局演示 (Fragment + XML)",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall
        )
    }

    HorizontalDivider()

    // Logout
    TextButton(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "退出登录",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
