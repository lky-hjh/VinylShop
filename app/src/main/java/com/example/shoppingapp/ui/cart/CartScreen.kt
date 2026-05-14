package com.example.shoppingapp.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.ui.components.CartItemRow
import com.example.shoppingapp.ui.components.ErrorView
import com.example.shoppingapp.ui.components.LoadingIndicator
import com.example.shoppingapp.ui.theme.contentHorizontalPadding
import com.example.shoppingapp.ui.theme.contentMaxWidth
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.toCurrencyString

@Composable
fun CartScreen(
    onCheckoutClick: () -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartState by viewModel.cartState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxContentWidth = contentMaxWidth()
        val horizPadding = contentHorizontalPadding()

        // 大屏居中 + 限制最大宽度
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (maxContentWidth != Dp.Unspecified) Alignment.TopCenter
                else Alignment.TopStart
        ) {
            val widthModifier = if (maxContentWidth != Dp.Unspecified) {
                Modifier
                    .fillMaxWidth()
                    .width(maxContentWidth)
            } else {
                Modifier.fillMaxWidth()
            }

            Column(
                modifier = Modifier
                    .then(widthModifier)
                    .fillMaxSize()
            ) {
                when (val state = cartState) {
                    is Resource.Loading -> LoadingIndicator()
                    is Resource.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadCart() })
                    is Resource.Success -> {
                        val items = state.data
                        if (items.isEmpty()) {
                            EmptyCart()
                        } else {
                            CartContent(
                                items = items,
                                viewModel = viewModel,
                                onProductClick = onProductClick,
                                onCheckoutClick = onCheckoutClick,
                                horizPadding = horizPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCart() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "购物车是空的",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "去逛逛黑胶唱片吧",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun CartContent(
    items: List<CartItem>,
    viewModel: CartViewModel,
    onProductClick: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    horizPadding: Dp = 16.dp
) {
    val selectedTotal = viewModel.getSelectedTotal()
    val selectedCount = viewModel.getSelectedCount()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "购物车 (${items.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { viewModel.clearCart() }) {
                Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("清空")
            }
        }

        HorizontalDivider()

        // Cart items
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = horizPadding, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it.id }) { item ->
                CartItemRow(
                    cartItem = item,
                    onCheckedChange = { /* TODO: update selection state */ },
                    onQuantityChange = { newQty ->
                        viewModel.updateQuantity(item.id, newQty)
                    },
                    onDelete = {
                        viewModel.removeItem(item.productId)
                    }
                )
            }
        }

        // Bottom bar
        HorizontalDivider()
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizPadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "合计",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        selectedTotal.toCurrencyString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = onCheckoutClick,
                    enabled = selectedCount > 0,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("去结算 ($selectedCount)")
                }
            }
        }
    }
}
