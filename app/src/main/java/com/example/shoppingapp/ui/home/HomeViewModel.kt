package com.example.shoppingapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.ProductRepository
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _featuredProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val featuredProducts: StateFlow<Resource<List<Product>>> = _featuredProducts.asStateFlow()

    private val _newProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val newProducts: StateFlow<Resource<List<Product>>> = _newProducts.asStateFlow()

    private val _genres = MutableStateFlow<Resource<List<String>>>(Resource.Loading)
    val genres: StateFlow<Resource<List<String>>> = _genres.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            productRepository.getFeaturedProducts().collect {
                _featuredProducts.value = it
            }
        }
        viewModelScope.launch {
            productRepository.getNewProducts().collect {
                _newProducts.value = it
            }
        }
        viewModelScope.launch {
            productRepository.getAllGenres().collect {
                _genres.value = it
            }
        }
    }
}
