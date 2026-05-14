package com.example.shoppingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.shoppingapp.ui.legacy.ProductDetailFragment
import com.example.shoppingapp.ui.legacy.ProductListFragment
import com.example.shoppingapp.ui.legacy.SimpleProduct

/**
 * 传统 Android 开发模式演示 Activity
 *
 * 演示知识点：
 * 1. Activity 生命周期（每个回调方法都有日志输出）
 * 2. Fragment 管理（FragmentManager + FragmentTransaction）
 * 3. Fragment 间通信（通过 Activity 接口回调）
 * 4. 事件处理：按钮点击监听器
 * 5. XML 布局文件设计界面
 * 6. 常用控件：TextView, Button, EditText, RecyclerView, ImageView
 */
class LegacyDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LegacyDemo"
    }

    private lateinit var tvLifecycleStatus: TextView
    private lateinit var tvFragmentStatus: TextView
    private lateinit var btnBackToList: Button
    private lateinit var tvSelectionHint: TextView

    // ========== Activity 生命周期回调 ==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 加载 XML 布局文件
        setContentView(R.layout.activity_legacy_demo)
        updateLifecycleStatus("onCreate")

        // 初始化传统 XML 控件
        tvLifecycleStatus = findViewById(R.id.tv_lifecycle_status)
        tvFragmentStatus = findViewById(R.id.tv_fragment_status)
        btnBackToList = findViewById(R.id.btn_back_to_list)
        tvSelectionHint = findViewById(R.id.tv_selection_hint)

        // 返回按钮事件监听
        btnBackToList.setOnClickListener {
            // Fragment 回退栈 — 演示 Fragment 管理
            supportFragmentManager.popBackStack()
            btnBackToList.isEnabled = false
            tvSelectionHint.text = "请选择一个唱片"
            updateFragmentStatus("已返回列表")
        }

        Log.d(TAG, "Activity onCreate 被调用")
    }

    override fun onStart() {
        super.onStart()
        updateLifecycleStatus("onStart")
        Log.d(TAG, "Activity onStart 被调用")
    }

    override fun onResume() {
        super.onResume()
        updateLifecycleStatus("onResume")
        Log.d(TAG, "Activity onResume 被调用")
    }

    override fun onPause() {
        super.onPause()
        updateLifecycleStatus("onPause")
        Log.d(TAG, "Activity onPause 被调用")
    }

    override fun onStop() {
        super.onStop()
        updateLifecycleStatus("onStop")
        Log.d(TAG, "Activity onStop 被调用")
    }

    override fun onDestroy() {
        super.onDestroy()
        updateLifecycleStatus("onDestroy")
        Log.d(TAG, "Activity onDestroy 被调用")
    }

    override fun onRestart() {
        super.onRestart()
        updateLifecycleStatus("onRestart")
        Log.d(TAG, "Activity onRestart 被调用")
    }

    // ========== Fragment 管理 ==========

    /**
     * 从列表 Fragment 跳转到详情 Fragment
     * 演示 Fragment 替换 + 回退栈 + 数据传递
     */
    fun navigateToDetail(product: SimpleProduct) {
        val detailFragment = ProductDetailFragment.newInstance(product)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack("product_detail")   // 加入回退栈
            .commit()

        // 更新 UI
        btnBackToList.isEnabled = true
        tvSelectionHint.text = "已选择: ${product.name}"
        updateFragmentStatus("详情页: ${product.name}")
    }

    // ========== UI 状态更新 ==========

    /**
     * 更新 Activity 生命周期状态显示
     */
    private fun updateLifecycleStatus(status: String) {
        if (::tvLifecycleStatus.isInitialized) {
            tvLifecycleStatus.text = "Activity: $status"
        }
    }

    /**
     * 更新 Fragment 状态显示（供 Fragment 调用）
     */
    fun onFragmentStatus(status: String) {
        if (::tvFragmentStatus.isInitialized) {
            tvFragmentStatus.text = "Fragment: $status"
        }
    }

    private fun updateFragmentStatus(status: String) {
        tvFragmentStatus.text = "Fragment: $status"
    }
}
