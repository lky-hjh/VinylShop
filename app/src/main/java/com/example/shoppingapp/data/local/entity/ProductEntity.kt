package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val artist: String,
    val genre: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val rating: Float = 0f,
    val stock: Int = 100,
    val releaseDate: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val isNew: Boolean = false
)
