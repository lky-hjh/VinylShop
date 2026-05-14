package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val orderNumber: String,
    val orderDate: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val status: String = "待支付",
    val shippingAddress: String = "",
    val shippingPhone: String = "",
    val shippingName: String = ""
)
