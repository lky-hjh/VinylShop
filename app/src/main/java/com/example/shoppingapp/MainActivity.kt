package com.example.shoppingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.shoppingapp.data.seed.SeedDataProvider
import com.example.shoppingapp.ui.navigation.AppNavHost
import com.example.shoppingapp.ui.navigation.Routes
import com.example.shoppingapp.ui.theme.VinylShopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject
    lateinit var seedDataProvider: SeedDataProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "========== Activity 生命周期 ==========")
        Log.d(TAG, "onCreate 被调用 - 应用首次创建")
        enableEdgeToEdge()

        // Seed data if first run
        seedDataProvider.seedIfNeeded()

        // Determine start destination
        val prefs = getSharedPreferences("vinylshop_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getString("user_id", null) != null
        val startDestination = if (isLoggedIn) Routes.MAIN else Routes.LOGIN
        Log.d(TAG, "登录状态: isLoggedIn=$isLoggedIn, 起始页面=$startDestination")

        setContent {
            VinylShopTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
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
