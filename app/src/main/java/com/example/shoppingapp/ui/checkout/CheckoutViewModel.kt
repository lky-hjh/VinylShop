package com.example.shoppingapp.ui.checkout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.CartRepository
import com.example.shoppingapp.data.repository.OrderRepository
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("vinylshop_prefs", Context.MODE_PRIVATE)

    private val _shippingName = MutableStateFlow("")
    val shippingName: StateFlow<String> = _shippingName.asStateFlow()

    private val _shippingPhone = MutableStateFlow("")
    val shippingPhone: StateFlow<String> = _shippingPhone.asStateFlow()

    private val _shippingAddress = MutableStateFlow("")
    val shippingAddress: StateFlow<String> = _shippingAddress.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    private val _orderState = MutableStateFlow<Resource<Order>?>(null)
    val orderState: StateFlow<Resource<Order>?> = _orderState.asStateFlow()

    private val userId: String
        get() = prefs.getString("user_id", "") ?: ""

    init {
        loadCartSummary()
    }

    private fun loadCartSummary() {
        viewModelScope.launch {
            val items = cartRepository.getCartItems(userId).first().getOrNull() ?: emptyList()
            val selected = items.filter { it.isSelected }
            _totalAmount.value = selected.sumOf { it.totalPrice }
            _itemCount.value = selected.size
        }
    }

    fun onNameChange(name: String) { _shippingName.value = name }
    fun onPhoneChange(phone: String) { _shippingPhone.value = phone }
    fun onAddressChange(address: String) { _shippingAddress.value = address }

    val isFormValid: Boolean
        get() = _shippingName.value.isNotBlank() &&
                _shippingPhone.value.isNotBlank() &&
                _shippingAddress.value.isNotBlank()

    fun submitOrder() {
        viewModelScope.launch {
            _orderState.value = Resource.Loading

            val items = cartRepository.getCartItems(userId).first().getOrNull() ?: return@launch
            val selectedItems = items.filter { it.isSelected }

            val itemList = selectedItems.map { Pair(it.productId, it.quantity) }
            val result = orderRepository.createOrder(
                userId = userId,
                shippingAddress = _shippingAddress.value,
                shippingPhone = _shippingPhone.value,
                shippingName = _shippingName.value,
                items = itemList
            )
            _orderState.value = result
        }
    }

    fun resetOrderState() {
        _orderState.value = null
    }
}
