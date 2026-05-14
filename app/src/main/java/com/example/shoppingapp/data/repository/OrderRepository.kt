package com.example.shoppingapp.data.repository

import com.example.shoppingapp.data.local.dao.CartDao
import com.example.shoppingapp.data.local.dao.OrderDao
import com.example.shoppingapp.data.local.dao.ProductDao
import com.example.shoppingapp.data.local.entity.OrderEntity
import com.example.shoppingapp.data.local.entity.OrderItemEntity
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderItem
import com.example.shoppingapp.domain.model.toOrder
import com.example.shoppingapp.domain.model.toOrderItem
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.generateOrderNumber
import com.example.shoppingapp.util.generateUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    fun getOrdersByUser(userId: String): Flow<Resource<List<Order>>> {
        return orderDao.getOrdersByUser(userId).map { entities ->
            val orders = entities.map { entity ->
                val items = orderDao.getOrderItems(entity.id).map { it.toOrderItem() }
                entity.toOrder(items)
            }
            Resource.success(orders)
        }
    }

    fun getOrdersByUserAndStatus(userId: String, status: String): Flow<Resource<List<Order>>> {
        return orderDao.getOrdersByUserAndStatus(userId, status).map { entities ->
            val orders = entities.map { entity ->
                val items = orderDao.getOrderItems(entity.id).map { it.toOrderItem() }
                entity.toOrder(items)
            }
            Resource.success(orders)
        }
    }

    fun getOrderById(orderId: String): Flow<Resource<Order?>> {
        return orderDao.getOrderByIdFlow(orderId).map { entity ->
            if (entity != null) {
                val items = orderDao.getOrderItems(entity.id).map { it.toOrderItem() }
                Resource.success(entity.toOrder(items))
            } else {
                Resource.success(null)
            }
        }
    }

    suspend fun createOrder(
        userId: String,
        shippingAddress: String,
        shippingPhone: String,
        shippingName: String,
        items: List<Pair<String, Int>>
    ): Resource<Order> {
        return try {
            val orderId = generateUUID()
            val orderNumber = generateOrderNumber()

            // Calculate total and build order items
            var totalAmount = 0.0
            val orderItems = mutableListOf<OrderItemEntity>()
            val productIdsToClear = mutableListOf<String>()

            for ((productId, quantity) in items) {
                val product = productDao.getProductById(productId)
                    ?: return Resource.error("商品不存在: $productId")

                if (product.stock < quantity) {
                    return Resource.error("${product.name} 库存不足")
                }

                val unitPrice = product.price
                totalAmount += unitPrice * quantity
                productIdsToClear.add(productId)

                orderItems.add(
                    OrderItemEntity(
                        id = generateUUID(),
                        orderId = orderId,
                        productId = productId,
                        productName = product.name,
                        productImage = product.imageUrl,
                        quantity = quantity,
                        unitPrice = unitPrice
                    )
                )

                // Decrement stock
                productDao.decrementStock(productId, quantity)
            }

            val order = OrderEntity(
                id = orderId,
                userId = userId,
                orderNumber = orderNumber,
                totalAmount = totalAmount,
                shippingAddress = shippingAddress,
                shippingPhone = shippingPhone,
                shippingName = shippingName
            )

            orderDao.insertOrderWithItems(order, orderItems)

            // Clear purchased items from cart
            for (productId in productIdsToClear) {
                cartDao.deleteByUserAndProduct(userId, productId)
            }

            val createdOrder = orderDao.getOrderById(orderId)!!
            val createdItems = orderDao.getOrderItems(orderId).map { it.toOrderItem() }
            Resource.success(createdOrder.toOrder(createdItems))
        } catch (e: Exception) {
            Resource.error("创建订单失败: ${e.message}", e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Resource<Unit> {
        return try {
            orderDao.updateOrderStatus(orderId, status)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("更新订单状态失败: ${e.message}", e)
        }
    }

    suspend fun deleteOrder(orderId: String): Resource<Unit> {
        return try {
            orderDao.deleteOrderWithItems(orderId)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("删除订单失败: ${e.message}", e)
        }
    }
}
