package com.example.shoppingapp.ui.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.OrderRepository
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("vinylshop_prefs", Context.MODE_PRIVATE)

    private val _ordersState = MutableStateFlow<Resource<List<Order>>>(Resource.Loading)
    val ordersState: StateFlow<Resource<List<Order>>> = _ordersState.asStateFlow()

    private val _orderDetailState = MutableStateFlow<Resource<Order?>>(Resource.Loading)
    val orderDetailState: StateFlow<Resource<Order?>> = _orderDetailState.asStateFlow()

    private val userId: String
        get() = prefs.getString("user_id", "") ?: ""

    init {
        loadOrders()
    }

    fun loadOrders() {
        if (userId.isEmpty()) return
        viewModelScope.launch {
            orderRepository.getOrdersByUser(userId).collect {
                _ordersState.value = it
            }
        }
    }

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).collect {
                _orderDetailState.value = it
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, "已取消")
            loadOrders()
        }
    }

    fun confirmReceived(orderId: String) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, "已完成")
            loadOrders()
        }
    }
}
