package com.example.shoppingapp.domain.model

import com.example.shoppingapp.data.local.entity.OrderEntity
import com.example.shoppingapp.data.local.entity.OrderItemEntity

data class Order(
    val id: String,
    val userId: String,
    val orderNumber: String,
    val orderDate: Long,
    val totalAmount: Double,
    val status: String,
    val shippingAddress: String,
    val shippingPhone: String,
    val shippingName: String,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val totalPrice: Double get() = unitPrice * quantity
}

fun OrderEntity.toOrder(items: List<OrderItem> = emptyList()): Order = Order(
    id = id,
    userId = userId,
    orderNumber = orderNumber,
    orderDate = orderDate,
    totalAmount = totalAmount,
    status = status,
    shippingAddress = shippingAddress,
    shippingPhone = shippingPhone,
    shippingName = shippingName,
    items = items
)

fun OrderItemEntity.toOrderItem(): OrderItem = OrderItem(
    id = id,
    orderId = orderId,
    productId = productId,
    productName = productName,
    productImage = productImage,
    quantity = quantity,
    unitPrice = unitPrice
)
