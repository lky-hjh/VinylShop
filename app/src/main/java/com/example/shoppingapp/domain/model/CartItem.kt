package com.example.shoppingapp.domain.model

data class CartItem(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productPrice: Double,
    val productStock: Int,
    val quantity: Int,
    val addedAt: Long,
    val isSelected: Boolean = true
) {
    val totalPrice: Double get() = productPrice * quantity
}
