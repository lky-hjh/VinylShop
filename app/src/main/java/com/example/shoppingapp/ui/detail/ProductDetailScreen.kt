package com.example.shoppingapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.ui.components.ErrorView
import com.example.shoppingapp.ui.components.LoadingIndicator
import com.example.shoppingapp.ui.theme.DetailLayoutType
import com.example.shoppingapp.ui.theme.detailLayoutType
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val productState by viewModel.productState.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val addToCartState by viewModel.addToCartState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(addToCartState) {
        when (addToCartState) {
            is Resource.Success -> {
                snackbarHostState.showSnackbar("已加入购物车")
                viewModel.resetAddToCartState()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar((addToCartState as Resource.Error).message)
                viewModel.resetAddToCartState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("商品详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = productState) {
            is Resource.Loading -> LoadingIndicator()
            is Resource.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadProduct(productId) })
            is Resource.Success -> {
                val product = state.data
                if (product != null) {
                    ProductDetailContent(
                        product = product,
                        quantity = quantity,
                        isAddingToCart = addToCartState is Resource.Loading,
                        onQuantityChange = { viewModel.setQuantity(it) },
                        onAddToCart = { viewModel.addToCart() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    quantity: Int,
    isAddingToCart: Boolean,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val layoutType = detailLayoutType()

        if (layoutType == DetailLayoutType.Horizontal) {
            // ========== 横屏/平板：双栏布局 ==========
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 左侧：图片（占 40% 宽度）
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                // 右侧：详情信息（占 60% 宽度）
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(24.dp)
                ) {
                    DetailInfoContent(
                        product = product,
                        quantity = quantity,
                        isAddingToCart = isAddingToCart,
                        onQuantityChange = onQuantityChange,
                        onAddToCart = onAddToCart
                    )
                }
            }
        } else {
            // ========== 手机竖屏：单栏布局 ==========
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Album cover
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    DetailInfoContent(
                        product = product,
                        quantity = quantity,
                        isAddingToCart = isAddingToCart,
                        onQuantityChange = onQuantityChange,
                        onAddToCart = onAddToCart
                    )
                }

                // Bottom action bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    AddToCartBar(
                        quantity = quantity,
                        stock = product.stock,
                        isAddingToCart = isAddingToCart,
                        onQuantityChange = onQuantityChange,
                        onAddToCart = onAddToCart
                    )
                }
            }
        }
    }
}

/**
 * 详情信息公共部分（手机/平板共用）
 */
@Composable
private fun DetailInfoContent(
    product: Product,
    quantity: Int,
    isAddingToCart: Boolean,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    // Name & Artist
    Text(
        text = product.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = product.artist,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Price & Rating
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.price.toCurrencyString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < product.rating.toInt()) Icons.Filled.Star
                        else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${product.rating}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Genre & Stock tags
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = product.genre,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (product.stock > 10)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = if (product.stock > 0) "库存 ${product.stock}" else "已售罄",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = if (product.stock > 10)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Description
    Text(
        text = "唱片简介",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = product.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 双栏布局时，把加入购物车按钮放在这里
    AddToCartBar(
        quantity = quantity,
        stock = product.stock,
        isAddingToCart = isAddingToCart,
        onQuantityChange = onQuantityChange,
        onAddToCart = onAddToCart
    )
}

/**
 * 加入购物车操作栏（共用组件）
 */
@Composable
private fun AddToCartBar(
    quantity: Int,
    stock: Int,
    isAddingToCart: Boolean,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quantity selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .then(
                    Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            FilledTonalButton(
                onClick = { onQuantityChange(quantity - 1) },
                enabled = quantity > 1,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Filled.Remove, "减少", modifier = Modifier.size(18.dp))
            }
            Text(
                text = "$quantity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            FilledTonalButton(
                onClick = { onQuantityChange(quantity + 1) },
                enabled = quantity < stock,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Filled.Add, "增加", modifier = Modifier.size(18.dp))
            }
        }

        Button(
            onClick = onAddToCart,
            enabled = stock > 0 && !isAddingToCart,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            if (isAddingToCart) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("加入购物车", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
