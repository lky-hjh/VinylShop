package com.example.shoppingapp.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.ui.components.ErrorView
import com.example.shoppingapp.ui.components.LoadingIndicator
import com.example.shoppingapp.ui.components.ProductCard
import com.example.shoppingapp.ui.theme.contentHorizontalPadding
import com.example.shoppingapp.ui.theme.gridMinCardWidth
import com.example.shoppingapp.util.Resource

@Composable
fun SearchScreen(
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val genresState by viewModel.genres.collectAsState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val horizPadding = contentHorizontalPadding()
        val cardMinWidth = gridMinCardWidth()

        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text("搜索唱片、艺术家...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "搜索")
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizPadding, vertical = 8.dp)
            )

            // Genre filter chips
            when (val state = genresState) {
                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = horizPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedGenre == null,
                                onClick = { viewModel.clearFilters() },
                                label = { Text("全部") }
                            )
                        }
                        items(state.data.size) { index ->
                            val genre = state.data[index]
                            FilterChip(
                                selected = selectedGenre == genre,
                                onClick = { viewModel.onGenreSelect(genre) },
                                label = { Text(genre) }
                            )
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results
            when {
                query.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "输入关键词搜索你喜欢的黑胶唱片",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
                searchResults is Resource.Loading -> LoadingIndicator()
                searchResults is Resource.Error -> ErrorView(
                    message = (searchResults as Resource.Error).message
                )
                searchResults is Resource.Success -> {
                    val products = (searchResults as Resource.Success).data
                    if (products.isEmpty()) {
                        Text(
                            text = "未找到相关唱片",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(32.dp)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(cardMinWidth),
                            contentPadding = PaddingValues(horizontal = horizPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(products) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
