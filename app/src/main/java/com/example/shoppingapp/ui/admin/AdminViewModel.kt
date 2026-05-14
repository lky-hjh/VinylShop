package com.example.shoppingapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.ProductRepository
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.generateUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 管理员商品管理 ViewModel
 * 实现商品的增删改查（CRUD）操作，体现管理员权限功能隔离
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<List<Product>>?>(null)
    val productsState: StateFlow<Resource<List<Product>>?> = _productsState.asStateFlow()

    private val _operationState = MutableStateFlow<Resource<String>?>(null)
    val operationState: StateFlow<Resource<String>?> = _operationState.asStateFlow()

    /** 当前正在编辑的商品（null 表示新增模式） */
    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    init {
        loadProducts()
    }

    /** 加载全部商品列表 */
    fun loadProducts() {
        viewModelScope.launch {
            _productsState.value = Resource.Loading
            productRepository.getAllProducts().collect { resource ->
                _productsState.value = resource
            }
        }
    }

    /** 删除商品 */
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = productRepository.deleteProduct(productId)
            _operationState.value = when (result) {
                is Resource.Success -> Resource.success("商品已删除")
                is Resource.Error -> Resource.error(result.message)
                else -> Resource.error("未知错误")
            }
        }
    }

    /** 进入编辑模式 */
    fun startEdit(product: Product) {
        _editingProduct.value = product
    }

    /** 进入新增模式 */
    fun startAdd() {
        _editingProduct.value = null
    }

    /** 保存商品（新增或更新） */
    fun saveProduct(
        name: String,
        artist: String,
        genre: String,
        price: Double,
        description: String,
        imageUrl: String,
        stock: Int,
        isFeatured: Boolean,
        isNew: Boolean
    ) {
        viewModelScope.launch {
            val existing = _editingProduct.value
            val product = if (existing != null) {
                // 更新模式
                existing.copy(
                    name = name,
                    artist = artist,
                    genre = genre,
                    price = price,
                    description = description,
                    imageUrl = imageUrl,
                    stock = stock,
                    isFeatured = isFeatured,
                    isNew = isNew
                )
            } else {
                // 新增模式
                Product(
                    id = generateUUID(),
                    name = name,
                    artist = artist,
                    genre = genre,
                    price = price,
                    description = description,
                    imageUrl = imageUrl,
                    rating = 0f,
                    stock = stock,
                    releaseDate = System.currentTimeMillis(),
                    isFeatured = isFeatured,
                    isNew = isNew
                )
            }

            val result = if (existing != null) {
                productRepository.updateProduct(product)
            } else {
                productRepository.addProduct(product)
            }

            _operationState.value = when (result) {
                is Resource.Success -> Resource.success(if (existing != null) "商品已更新" else "商品已添加")
                is Resource.Error -> Resource.error(result.message)
                else -> Resource.error("未知错误")
            }
            _editingProduct.value = null
        }
    }

    /** 清除操作状态 */
    fun clearOperationState() {
        _operationState.value = null
    }
}
