package com.example.shoppingapp.ui.legacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R

/**
 * RecyclerView 适配器
 * 演示传统 Android 控件 RecyclerView + Adapter 模式
 */
class ProductAdapter(
    private var products: List<SimpleProduct>,
    private val onItemClick: (SimpleProduct) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, onItemClick)
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<SimpleProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.tv_product_name)
        private val artistText: TextView = itemView.findViewById(R.id.tv_product_artist)
        private val priceText: TextView = itemView.findViewById(R.id.tv_product_price)

        fun bind(product: SimpleProduct, onItemClick: (SimpleProduct) -> Unit) {
            nameText.text = product.name
            artistText.text = product.artist
            priceText.text = "¥${String.format("%.0f", product.price)}"
            itemView.setOnClickListener { onItemClick(product) }
        }
    }
}

/**
 * 简单的商品数据类（用于传统布局演示）
 */
data class SimpleProduct(
    val id: String,
    val name: String,
    val artist: String,
    val genre: String,
    val price: Double,
    val description: String,
    val rating: Float,
    val stock: Int
)
