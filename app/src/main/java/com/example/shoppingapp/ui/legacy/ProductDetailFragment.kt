package com.example.shoppingapp.ui.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shoppingapp.LegacyDemoActivity
import com.example.shoppingapp.R

/**
 * Fragment 示例 — 商品详情页
 *
 * 演示知识点：
 * - Fragment 间数据传递（通过 Bundle / Activity 接口）
 * - 传统控件使用：TextView, Button, ImageView
 * - 事件处理：Button onClick 基于监听的方式
 * - Fragment 与 Activity 通信
 */
class ProductDetailFragment : Fragment() {

    companion object {
        private const val ARG_PRODUCT = "arg_product"

        /**
         * 通过 Bundle 传递参数给 Fragment
         * 演示 Fragment 实例化 + 数据传递
         */
        fun newInstance(product: SimpleProduct): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putString("product_id", product.id)
            args.putString("product_name", product.name)
            args.putString("product_artist", product.artist)
            args.putDouble("product_price", product.price)
            args.putString("product_description", product.description)
            args.putString("product_genre", product.genre)
            args.putFloat("product_rating", product.rating)
            args.putInt("product_stock", product.stock)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProductData(view)
        setupAddToCart(view)

        // 通知 Activity 当前 Fragment 状态
        (activity as? LegacyDemoActivity)?.onFragmentStatus("详情页已加载")
    }

    /**
     * 从 Bundle 获取数据，填充到各 TextView 控件
     */
    private fun loadProductData(view: View) {
        val args = arguments ?: return

        view.findViewById<TextView>(R.id.tv_detail_name).text =
            args.getString("product_name", "未知唱片")
        view.findViewById<TextView>(R.id.tv_detail_artist).text =
            args.getString("product_artist", "未知艺术家")
        view.findViewById<TextView>(R.id.tv_detail_price).text =
            "¥${String.format("%.0f", args.getDouble("product_price", 0.0))}"
        view.findViewById<TextView>(R.id.tv_detail_description).text =
            args.getString("product_description", "暂无介绍")
        view.findViewById<TextView>(R.id.tv_detail_genre).text =
            args.getString("product_genre", "未知")
        view.findViewById<TextView>(R.id.tv_detail_rating).text =
            "评分: ${args.getFloat("product_rating", 0f)}"
        view.findViewById<TextView>(R.id.tv_detail_stock).text =
            "库存: ${args.getInt("product_stock", 0)}"

        // 共享数据示例：显示从列表 Fragment 传来的商品 ID
        view.findViewById<TextView>(R.id.tv_shared_data).text =
            "从列表 Fragment 传递的商品 ID: ${args.getString("product_id", "")}"

        // 图片使用默认占位图（纯 XML 演示，不加载网络图片）
        view.findViewById<ImageView>(R.id.iv_detail_image).setImageResource(
            android.R.drawable.ic_menu_gallery
        )
    }

    /**
     * Button 事件处理 — 基于监听的方式
     * 演示 Android 传统的事件处理机制
     */
    private fun setupAddToCart(view: View) {
        view.findViewById<Button>(R.id.btn_add_to_cart).setOnClickListener {
            val productName = arguments?.getString("product_name", "")
            Toast.makeText(
                requireContext(),
                "已加入购物车: $productName（传统布局演示）",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? LegacyDemoActivity)?.onFragmentStatus("详情页可见 (onResume)")
    }
}
