package com.example.shoppingapp.util

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun errorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> error(message: String, throwable: Throwable? = null): Resource<T> =
            Error(message, throwable)

        fun <T> loading(): Resource<T> = Loading
    }
}
