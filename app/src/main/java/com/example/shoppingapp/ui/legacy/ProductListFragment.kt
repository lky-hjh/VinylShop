package com.example.shoppingapp.ui.legacy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.LegacyDemoActivity
import com.example.shoppingapp.R

/**
 * Fragment 示例 — 商品列表页
 *
 * 演示知识点：
 * - Fragment 生命周期（onCreateView, onViewCreated）
 * - Fragment 间通信（通过 Activity 传递数据）
 * - RecyclerView + Adapter 的使用
 * - EditText 事件监听（TextWatcher）
 * - XML 布局文件加载
 */
class ProductListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var searchInput: EditText

    /**
     * 所有产品数据（硬编码用于演示）
     * 展示传统方式在 Fragment 中管理数据
     */
    private val allProducts = listOf(
        SimpleProduct("vinyl_001", "Abbey Road", "The Beatles", "摇滚", 299.0,
            "披头士乐队的经典专辑，录制于1969年。", 4.9f, 50),
        SimpleProduct("vinyl_002", "Kind of Blue", "Miles Davis", "爵士", 259.0,
            "迈尔斯·戴维斯的杰作，被誉为爵士乐史上最重要的专辑。", 4.8f, 35),
        SimpleProduct("vinyl_003", "Thriller", "Michael Jackson", "流行", 329.0,
            "流行音乐之王的巅峰之作，全球销量最高的专辑。", 4.9f, 60),
        SimpleProduct("vinyl_004", "The Dark Side of the Moon", "Pink Floyd", "摇滚", 319.0,
            "平克·弗洛伊德的旷世杰作。", 4.9f, 45),
        SimpleProduct("vinyl_005", "Rumours", "Fleetwood Mac", "摇滚", 279.0,
            "史上最畅销的专辑之一。", 4.7f, 40),
        SimpleProduct("vinyl_006", "Random Access Memories", "Daft Punk", "电子", 349.0,
            "格莱美年度专辑。", 4.8f, 30),
        SimpleProduct("vinyl_007", "Blue Train", "John Coltrane", "爵士", 269.0,
            "硬波普爵士的巅峰之作。", 4.7f, 20),
        SimpleProduct("vinyl_008", "21", "Adele", "流行", 289.0,
            "阿黛尔的突破性专辑。", 4.6f, 55),
        SimpleProduct("vinyl_015", "Currents", "Tame Impala", "电子", 279.0,
            "迷幻流行经典。", 4.7f, 25),
        SimpleProduct("vinyl_017", "Igor", "Tyler, the Creator", "嘻哈", 289.0,
            "格莱美最佳说唱专辑。", 4.6f, 20),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 加载 XML 布局文件
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(view)
        initSearch(view)

        // 通知 Activity 当前 Fragment 状态
        (activity as? LegacyDemoActivity)?.onFragmentStatus("列表页已加载")
    }

    private fun initRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.rv_product_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProductAdapter(allProducts) { product ->
            // 通过 Activity 切换到详情 Fragment
            (activity as? LegacyDemoActivity)?.navigateToDetail(product)
        }
        recyclerView.adapter = adapter
    }

    /**
     * EditText 事件监听（TextWatcher）
     * 演示基于监听的事件处理机制
     */
    private fun initSearch(view: View) {
        searchInput = view.findViewById(R.id.et_search)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                val filtered = if (query.isEmpty()) {
                    allProducts
                } else {
                    allProducts.filter {
                        it.name.lowercase().contains(query) ||
                        it.artist.lowercase().contains(query)
                    }
                }
                adapter.updateData(filtered)
            }
        })
    }

    /**
     * 当 Fragment 恢复可见时
     */
    override fun onResume() {
        super.onResume()
        (activity as? LegacyDemoActivity)?.onFragmentStatus("列表页可见 (onResume)")
    }
}
