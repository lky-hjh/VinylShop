package com.example.shoppingapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
fun HomeScreen(
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val featuredState by viewModel.featuredProducts.collectAsState()
    val newState by viewModel.newProducts.collectAsState()
    val genresState by viewModel.genres.collectAsState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val horizPadding = contentHorizontalPadding()
        val cardMinWidth = gridMinCardWidth()
        // 捕获 maxWidth 以避免 when 块中的隐式接收器歧义
        val screenMaxWidth = maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Banner
            HeroBanner(modifier = Modifier.padding(horizontal = horizPadding))

            Spacer(modifier = Modifier.height(20.dp))

            // Genres horizontal scroll
            SectionTitle("音乐流派", modifier = Modifier.padding(horizontal = horizPadding))
            when (val state = genresState) {
                is Resource.Loading -> {}
                is Resource.Success -> GenreChips(
                    genres = state.data,
                    contentPadding = PaddingValues(horizontal = horizPadding)
                )
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Featured products
            SectionTitle("精选推荐", modifier = Modifier.padding(horizontal = horizPadding))
            when (val state = featuredState) {
                is Resource.Loading -> LoadingIndicator()
                is Resource.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadData() })
                is Resource.Success -> ProductGrid(
                    products = state.data,
                    onProductClick = onProductClick,
                    horizontalPadding = horizPadding,
                    minCardWidth = cardMinWidth,
                    // 计算高度所需的列数：最大可用宽度 ÷ (卡片最小宽度 + 间距)
                    columnCount = ((screenMaxWidth - horizPadding * 2 + 12.dp) / (cardMinWidth + 12.dp))
                        .toInt()
                        .coerceAtLeast(1)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // New arrivals
            SectionTitle("新品上市", modifier = Modifier.padding(horizontal = horizPadding))
            when (val state = newState) {
                is Resource.Loading -> {}
                is Resource.Error -> {}
                is Resource.Success -> ProductGrid(
                    products = state.data,
                    onProductClick = onProductClick,
                    horizontalPadding = horizPadding,
                    minCardWidth = cardMinWidth,
                    columnCount = ((screenMaxWidth - horizPadding * 2 + 12.dp) / (cardMinWidth + 12.dp))
                        .toInt()
                        .coerceAtLeast(1)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HeroBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        Color(0xFF4A148C)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "黑胶复兴计划",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "探索经典 · 发现新声 · 收藏永恒",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun GenreChips(
    genres: List<String>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres.size) { index ->
            FilterChip(
                selected = false,
                onClick = { /* Navigate to search with genre filter */ },
                label = { Text(genres[index]) }
            )
        }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    horizontalPadding: Dp = 16.dp,
    minCardWidth: Dp = 160.dp,
    columnCount: Int = 2
) {
    if (products.isEmpty()) {
        Text(
            text = "暂无商品",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )
        return
    }

    // 根据实际列数计算网格高度（固定高度 + 内嵌滚动列）
    val gridHeight = 280.dp * ((products.size + columnCount - 1) / columnCount)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minCardWidth),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(gridHeight),
        userScrollEnabled = false
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}
