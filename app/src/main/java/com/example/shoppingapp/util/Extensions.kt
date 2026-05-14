package com.example.shoppingapp.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun Double.toCurrencyString(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.CHINA)
    return format.format(this)
}

fun Long.toDateString(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}

fun generateOrderNumber(): String {
    val now = System.currentTimeMillis()
    val random = (1000..9999).random()
    return "ORD${now}${random}"
}

fun generateUUID(): String = UUID.randomUUID().toString().replace("-", "").take(16)

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.length >= 6
}

fun String.isValidUsername(): Boolean {
    return this.length in 3..20 && this.all { it.isLetterOrDigit() || it == '_' }
}
