package com.example.shoppingapp.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.CartRepository
import com.example.shoppingapp.data.repository.ProductRepository
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("vinylshop_prefs", Context.MODE_PRIVATE)

    private val _productState = MutableStateFlow<Resource<Product?>>(Resource.Loading)
    val productState: StateFlow<Resource<Product?>> = _productState.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _addToCartState = MutableStateFlow<Resource<Unit>?>(null)
    val addToCartState: StateFlow<Resource<Unit>?> = _addToCartState.asStateFlow()

    private val userId: String
        get() = prefs.getString("user_id", "") ?: ""

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            productRepository.getProductByIdFlow(productId).collect {
                _productState.value = it
            }
        }
    }

    fun setQuantity(qty: Int) {
        if (qty in 1..(_productState.value.getOrNull()?.stock ?: 1)) {
            _quantity.value = qty
        }
    }

    fun addToCart() {
        val productId = _productState.value.getOrNull()?.id ?: return
        if (userId.isEmpty()) return
        viewModelScope.launch {
            _addToCartState.value = Resource.Loading
            val result = cartRepository.addToCart(userId, productId, _quantity.value)
            _addToCartState.value = result
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = null
    }
}
