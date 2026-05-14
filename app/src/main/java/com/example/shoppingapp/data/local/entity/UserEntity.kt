package com.example.shoppingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val createdAt: Long = System.currentTimeMillis()
)
