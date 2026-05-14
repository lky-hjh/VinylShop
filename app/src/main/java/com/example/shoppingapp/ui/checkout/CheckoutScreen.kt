package com.example.shoppingapp.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.ui.theme.contentHorizontalPadding
import com.example.shoppingapp.ui.theme.contentMaxWidth
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onOrderSuccess: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val name by viewModel.shippingName.collectAsState()
    val phone by viewModel.shippingPhone.collectAsState()
    val address by viewModel.shippingAddress.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val itemCount by viewModel.itemCount.collectAsState()
    val orderState by viewModel.orderState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderState) {
        when (orderState) {
            is Resource.Error -> {
                snackbarHostState.showSnackbar((orderState as Resource.Error).message)
                viewModel.resetOrderState()
            }
            is Resource.Success -> {
                val orderId = (orderState as Resource.Success).data.id
                onOrderSuccess(orderId)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("确认订单") },
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                        .verticalScroll(rememberScrollState())
                ) {
                    // Shipping info
                    Text(
                        "收货信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = horizPadding, vertical = 16.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("收货人") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizPadding)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = { Text("手机号") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizPadding)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = viewModel::onAddressChange,
                        label = { Text("详细地址") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizPadding)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Order summary
                    Text(
                        "订单信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = horizPadding)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizPadding),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("商品件数", style = MaterialTheme.typography.bodyLarge)
                                Text("$itemCount 件", style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "订单总额",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    totalAmount.toCurrencyString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Submit button
                    Button(
                        onClick = { viewModel.submitOrder() },
                        enabled = viewModel.isFormValid && orderState !is Resource.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizPadding)
                            .height(50.dp)
                    ) {
                        if (orderState is Resource.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("提交订单", style = MaterialTheme.typography.titleSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
