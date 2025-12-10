package com.example.level_up_app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.level_up_app.data.Product
import com.example.level_up_app.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: AdminProductsViewModel = remember { AdminProductsViewModel() },
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes de éxito o error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isOperationSuccess) {
        if (uiState.isOperationSuccess) {
            snackbarHostState.showSnackbar("Operación realizada exitosamente")
            viewModel.clearOperationSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar Productos") },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.products.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay productos registrados",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar primer producto")
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.products) { product ->
                        AdminProductCard(
                            product = product,
                            onEdit = {
                                selectedProduct = product
                                showEditDialog = true
                            },
                            onDelete = {
                                productToDelete = product
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogos
    if (showAddDialog) {
        ProductFormDialog(
            product = null,
            categories = uiState.categories,
            onDismiss = { showAddDialog = false },
            onSave = { product ->
                viewModel.addProduct(product)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && selectedProduct != null) {
        ProductFormDialog(
            product = selectedProduct,
            categories = uiState.categories,
            onDismiss = {
                showEditDialog = false
                selectedProduct = null
            },
            onSave = { product ->
                viewModel.updateProduct(product)
                showEditDialog = false
                selectedProduct = null
            }
        )
    }

    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                productToDelete = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el producto '${productToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        productToDelete?.let { viewModel.deleteProduct(it.id) }
                        showDeleteDialog = false
                        productToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        productToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            if (product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageUtils.getImageUrl(product.imageUrl),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${formatPrice(product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Stock: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Función auxiliar para formatear precios
fun formatPrice(price: Double): String {
    val priceStr = price.toLong().toString()
    return priceStr.reversed().chunked(3).joinToString(".").reversed()
}
