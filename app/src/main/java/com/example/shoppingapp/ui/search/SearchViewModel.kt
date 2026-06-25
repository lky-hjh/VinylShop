package com.example.shoppingapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.repository.ProductRepository
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _searchResults = MutableStateFlow<Resource<List<Product>>?>(null)
    val searchResults: StateFlow<Resource<List<Product>>?> = _searchResults.asStateFlow()

    private val _genres = MutableStateFlow<Resource<List<String>>>(Resource.Loading)
    val genres: StateFlow<Resource<List<String>>> = _genres.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            productRepository.getAllGenres().collect {
                _genres.value = it
            }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            performSearch()
        }
    }

    fun onGenreSelect(genre: String?) {
        _selectedGenre.value = genre
        if (_searchQuery.value.isNotBlank()) {
            performSearch()
        }
    }

    /** 仅按流派搜索（首页流派标签跳转时使用） */
    fun searchByGenre(genre: String) {
        _selectedGenre.value = genre
        viewModelScope.launch {
            productRepository.searchProductsWithFilter(
                query = "",
                genre = genre,
                minPrice = 0.0,
                maxPrice = 99999.0
            ).collect {
                _searchResults.value = it
            }
        }
    }

    private fun performSearch() {
        val query = _searchQuery.value
        val genre = _selectedGenre.value
        viewModelScope.launch {
            productRepository.searchProductsWithFilter(
                query = query,
                genre = genre,
                minPrice = 0.0,
                maxPrice = 99999.0
            ).collect {
                _searchResults.value = it
            }
        }
    }

    fun clearFilters() {
        _selectedGenre.value = null
        if (_searchQuery.value.isNotBlank()) {
            performSearch()
        }
    }
}
