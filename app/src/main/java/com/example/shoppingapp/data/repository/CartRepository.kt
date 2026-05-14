package com.example.shoppingapp.data.repository

import com.example.shoppingapp.data.local.dao.CartDao
import com.example.shoppingapp.data.local.dao.ProductDao
import com.example.shoppingapp.data.local.entity.CartItemEntity
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.generateUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    fun getCartItems(userId: String): Flow<Resource<List<CartItem>>> {
        return cartDao.getCartItemsByUser(userId).map { cartEntities ->
            val cartItems = cartEntities.mapNotNull { cartEntity ->
                val product = productDao.getProductById(cartEntity.productId)
                product?.let {
                    CartItem(
                        id = cartEntity.id,
                        userId = cartEntity.userId,
                        productId = cartEntity.productId,
                        productName = it.name,
                        productImage = it.imageUrl,
                        productPrice = it.price,
                        productStock = it.stock,
                        quantity = cartEntity.quantity,
                        addedAt = cartEntity.addedAt
                    )
                }
            }
            Resource.success(cartItems)
        }
    }

    fun getCartCount(userId: String): Flow<Int> {
        return cartDao.getCartCount(userId)
    }

    suspend fun addToCart(userId: String, productId: String, quantity: Int = 1): Resource<Unit> {
        return try {
            val existing = cartDao.getCartItem(userId, productId)
            if (existing != null) {
                cartDao.update(existing.copy(quantity = existing.quantity + quantity))
            } else {
                cartDao.insert(
                    CartItemEntity(
                        id = generateUUID(),
                        userId = userId,
                        productId = productId,
                        quantity = quantity
                    )
                )
            }
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("添加到购物车失败: ${e.message}", e)
        }
    }

    suspend fun updateQuantity(cartItemId: String, quantity: Int): Resource<Unit> {
        return try {
            val entity = cartDao.getById(cartItemId)
                ?: return Resource.error("购物车项不存在")
            cartDao.update(entity.copy(quantity = quantity))
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("更新数量失败: ${e.message}", e)
        }
    }

    suspend fun removeFromCart(userId: String, productId: String): Resource<Unit> {
        return try {
            cartDao.deleteByUserAndProduct(userId, productId)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("移除商品失败: ${e.message}", e)
        }
    }

    suspend fun clearCart(userId: String): Resource<Unit> {
        return try {
            cartDao.clearCart(userId)
            Resource.success(Unit)
        } catch (e: Exception) {
            Resource.error("清空购物车失败: ${e.message}", e)
        }
    }
}
