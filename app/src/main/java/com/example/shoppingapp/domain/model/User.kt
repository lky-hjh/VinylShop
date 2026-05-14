package com.example.shoppingapp.domain.model

import com.example.shoppingapp.data.local.entity.UserEntity

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val avatar: String,
    val address: String,
    val createdAt: Long
)

fun UserEntity.toUser(): User = User(
    id = id,
    username = username,
    email = email,
    password = password,
    phone = phone,
    avatar = avatar,
    address = address,
    createdAt = createdAt
)
