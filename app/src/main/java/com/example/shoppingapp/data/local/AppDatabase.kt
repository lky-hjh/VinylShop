package com.example.shoppingapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shoppingapp.data.local.dao.CartDao
import com.example.shoppingapp.data.local.dao.OrderDao
import com.example.shoppingapp.data.local.dao.ProductDao
import com.example.shoppingapp.data.local.dao.UserDao
import com.example.shoppingapp.data.local.entity.CartItemEntity
import com.example.shoppingapp.data.local.entity.OrderEntity
import com.example.shoppingapp.data.local.entity.OrderItemEntity
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.entity.UserEntity

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
}
