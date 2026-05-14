package com.example.shoppingapp.ui.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.CartRepository
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("vinylshop_prefs", Context.MODE_PRIVATE)

    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading)
    val cartState: StateFlow<Resource<List<CartItem>>> = _cartState.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val userId: String
        get() = prefs.getString("user_id", "") ?: ""

    init {
        loadCart()
    }

    fun loadCart() {
        if (userId.isEmpty()) return
        viewModelScope.launch {
            cartRepository.getCartItems(userId).collect {
                _cartState.value = it
                _cartCount.value = (it.getOrNull()?.size ?: 0)
            }
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) return
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.updateQuantity(cartItemId, quantity)
            _isLoading.value = false
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            cartRepository.removeFromCart(userId, productId)
            _isLoading.value = false
        }
    }

    fun getSelectedTotal(): Double {
        val items = _cartState.value.getOrNull() ?: return 0.0
        return items.filter { it.isSelected }.sumOf { it.totalPrice }
    }

    fun getSelectedCount(): Int {
        val items = _cartState.value.getOrNull() ?: return 0
        return items.count { it.isSelected }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
        }
    }
}
