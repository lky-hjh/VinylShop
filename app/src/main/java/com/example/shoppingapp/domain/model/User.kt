package com.example.shoppingapp.domain.model

import com.example.shoppingapp.data.local.entity.UserEntity
import com.example.shoppingapp.data.local.entity.UserRole

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val avatar: String,
    val address: String,
    val role: String = UserRole.USER,
    val createdAt: Long
) {
    val isAdmin: Boolean get() = role == UserRole.ADMIN
}

fun UserEntity.toUser(): User = User(
    id = id,
    username = username,
    email = email,
    password = password,
    phone = phone,
    avatar = avatar,
    address = address,
    role = role,
    createdAt = createdAt
)
