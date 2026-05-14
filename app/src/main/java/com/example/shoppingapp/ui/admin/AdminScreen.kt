package com.example.shoppingapp.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.util.Resource
import com.example.shoppingapp.util.toCurrencyString

/**
 * 管理员商品管理页面
 * 仅管理员可访问，体现权限管理的功能隔离
 * 支持商品的新增、编辑、删除操作（完整 CRUD）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBackClick: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val productsState by viewModel.productsState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val editingProduct by viewModel.editingProduct.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    // 处理操作结果
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is Resource.Success -> {
                snackbarHostState.showSnackbar(state.data)
                viewModel.clearOperationState()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearOperationState()
            }
            else -> {}
        }
    }

    // 编辑/新增弹窗
    if (showEditDialog) {
        ProductEditDialog(
            product = editingProduct,
            onDismiss = {
                showEditDialog = false
                viewModel.startAdd()
            },
            onSave = { name, artist, genre, price, description, imageUrl, stock, isFeatured, isNew ->
                viewModel.saveProduct(name, artist, genre, price, description, imageUrl, stock, isFeatured, isNew)
                showEditDialog = false
            }
        )
    }

    // 删除确认弹窗
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除「${product.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("商品管理（管理员）") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startAdd()
                    showEditDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "添加商品")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = productsState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = "加载失败: ${state.message}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Resource.Success -> {
                    val products = state.data
                    if (products.isEmpty()) {
                        Text(
                            text = "暂无商品，点击右下角 + 添加",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            )
                        ) {
                            items(products, key = { it.id }) { product ->
                                AdminProductItem(
                                    product = product,
                                    onEdit = {
                                        viewModel.startEdit(product)
                                        showEditDialog = true
                                    },
                                    onDelete = { showDeleteDialog = product }
                                )
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}

/**
 * 管理员商品列表项
 */
@Composable
private fun AdminProductItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 商品信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.price.toCurrencyString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "库存: ${product.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    if (product.isFeatured) {
                        Text(
                            text = "精选",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 操作按钮
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "编辑",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 商品编辑/新增弹窗
 */
@Composable
private fun ProductEditDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (name: String, artist: String, genre: String, price: Double,
             description: String, imageUrl: String, stock: Int,
             isFeatured: Boolean, isNew: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var artist by remember { mutableStateOf(product?.artist ?: "") }
    var genre by remember { mutableStateOf(product?.genre ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "100") }
    var isFeatured by remember { mutableStateOf(product?.isFeatured ?: false) }
    var isNew by remember { mutableStateOf(product?.isNew ?: false) }

    val isEdit = product != null
    val isValid = name.isNotBlank() && artist.isNotBlank() && genre.isNotBlank()
            && price.toDoubleOrNull() != null && stock.toIntOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "编辑商品" else "添加商品") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("唱片名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("艺术家") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("流派") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("价格") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("图片URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("库存") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = isFeatured, onCheckedChange = { isFeatured = it })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("精选", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = isNew, onCheckedChange = { isNew = it })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("新品", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        name.trim(), artist.trim(), genre.trim(),
                        price.toDoubleOrNull() ?: 0.0,
                        description.trim(), imageUrl.trim(),
                        stock.toIntOrNull() ?: 0,
                        isFeatured, isNew
                    )
                },
                enabled = isValid
            ) {
                Text(if (isEdit) "保存" else "添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
