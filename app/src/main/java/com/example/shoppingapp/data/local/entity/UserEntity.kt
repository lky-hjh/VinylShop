package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户角色枚举
 */
object UserRole {
    const val USER = "USER"
    const val ADMIN = "ADMIN"
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String = "",
    val avatar: String = "",
    val address: String = "",
    val role: String = UserRole.USER,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean get() = role == UserRole.ADMIN
}
