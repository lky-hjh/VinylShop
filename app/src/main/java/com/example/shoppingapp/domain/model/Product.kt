package com.example.shoppingapp.domain.model

import com.example.shoppingapp.data.local.entity.ProductEntity

data class Product(
    val id: String,
    val name: String,
    val artist: String,
    val genre: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val rating: Float,
    val stock: Int,
    val releaseDate: Long,
    val isFeatured: Boolean,
    val isNew: Boolean
)

fun ProductEntity.toProduct(): Product = Product(
    id = id,
    name = name,
    artist = artist,
    genre = genre,
    price = price,
    description = description,
    imageUrl = imageUrl,
    rating = rating,
    stock = stock,
    releaseDate = releaseDate,
    isFeatured = isFeatured,
    isNew = isNew
)
