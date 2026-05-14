package com.example.shoppingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.shoppingapp.data.local.dao.UserDao
import com.example.shoppingapp.data.seed.SeedDataProvider
import com.example.shoppingapp.ui.navigation.AppNavHost
import com.example.shoppingapp.ui.navigation.Routes
import com.example.shoppingapp.ui.theme.VinylShopTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject
    lateinit var seedDataProvider: SeedDataProvider

    @Inject
    lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "========== Activity 生命周期 ==========")
        Log.d(TAG, "onCreate 被调用 - 应用首次创建")
        enableEdgeToEdge()

        // 使用可变状态控制启动目标，初始为 null 表示正在验证
        var startDestination by mutableStateOf<String?>(null)

        // 异步验证用户身份后设置启动目标
        lifecycleScope.launch {
            val dest = resolveStartDestination()
            startDestination = dest
        }

        setContent {
            VinylShopTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val dest = startDestination
                    if (dest == null) {
                        // ⏳ 正在验证登录态 — 显示加载屏
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        val navController = rememberNavController()
                        AppNavHost(
                            navController = navController,
                            startDestination = dest
                        )
                    }
                }
            }
        }
    }

    /**
     * 解析启动目标：
     * 1. 先保证种子数据已写入
     * 2. 如果 prefs 存储了 userId，验证该用户是否仍在 Room 数据库中
     * 3. 用户不存在 → 清除无效 prefs → 返回 LOGIN
     * 4. 用户存在 → 返回 MAIN
     * 5. 未登录 → 返回 LOGIN
     */
    private suspend fun resolveStartDestination(): String = withContext(Dispatchers.IO) {
        // 1. 确保种子数据完成（必须等 Room 写入完毕）
        seedDataProvider.seedIfNeeded()

        val prefs = getSharedPreferences("vinylshop_prefs", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)

        if (userId != null) {
            // 2. 验证用户是否真的存在于 Room 中
            val user = userDao.getUserById(userId)
            if (user == null) {
                Log.w(TAG, "⛔ prefs 中的 userId=$userId 在 DB 中不存在 → 清除无效登录态，跳转登录页")
                prefs.edit().clear().apply()
                Routes.LOGIN
            } else {
                Log.d(TAG, "✅ userId=$userId 验证通过 → 直接进入主页")
                Routes.MAIN
            }
        } else {
            Log.d(TAG, "ℹ️ 用户未登录 → 跳转登录页")
            Routes.LOGIN
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart 被调用 - 应用即将对用户可见")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume 被调用 - 应用已获得焦点，可与用户交互")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause 被调用 - 应用失去焦点（如弹出对话框、跳转其他页面）")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop 被调用 - 应用对用户不可见")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy 被调用 - 应用被销毁")
        Log.d(TAG, "========== Activity 生命周期结束 ==========")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart 被调用 - 应用从停止状态重新启动")
    }
}
