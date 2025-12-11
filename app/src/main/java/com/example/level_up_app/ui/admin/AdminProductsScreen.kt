package com.example.level_up_app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.level_up_app.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    onBack: () -> Unit,
    viewModel: AdminProductsViewModel = remember { AdminProductsViewModel() }
) {
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var showProductForm by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    if (showProductForm) {
        ProductFormScreen(
            productToEdit = productToEdit,
            viewModel = viewModel,
            categories = uiState.categories,
            onBack = {
                showProductForm = false
                productToEdit = null
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Administrar Productos") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        // Filtro Activos/Descontinuados
                        FilterChip(
                            selected = uiState.showDiscontinuedOnly,
                            onClick = { viewModel.toggleFilter() },
                            label = {
                                Text(if (uiState.showDiscontinuedOnly) "Activos" else "Descontinuados" )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (uiState.showDiscontinuedOnly)
                                        Icons.Default.Clear else Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    productToEdit = null
                    showProductForm = true
                }) {
                    Icon(Icons.Default.Add, "Crear Producto")
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.productsList.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No hay productos", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.productsList) { product ->
                                AdminProductCard(
                                    product = product,
                                    onEdit = {
                                        productToEdit = product
                                        showProductForm = true
                                    },
                                    onDiscontinue = { viewModel.showDiscontinueDialog(product) },
                                    onReactivate = { viewModel.reactivateProduct(product.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Diálogo de confirmación de descontinuar
            if (uiState.showDiscontinueDialog && uiState.productToDiscontinue != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.hideDiscontinueDialog() },
                    icon = {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    title = { Text("Descontinuar Producto") },
                    text = {
                        Text("¿Deseas descontinuar \"${uiState.productToDiscontinue?.name}\"? El producto dejará de estar disponible para compra.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.discontinueProduct(uiState.productToDiscontinue!!.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Descontinuar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.hideDiscontinueDialog() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDiscontinue: () -> Unit,
    onReactivate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.discontinued == true)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Badge de estado
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = if (product.discontinued == true) "DESCONTINUADO" else "ACTIVO",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (product.discontinued == true)
                                    MaterialTheme.colorScheme.errorContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                labelColor = if (product.discontinued == true)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${product.currency} ${product.price}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Stock: ${product.stock} • ${product.category?.name ?: "Sin categoría"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }

                // Botón condicional: Descontinuar o Reactivar
                if (product.discontinued == true) {
                    Button(
                        onClick = onReactivate,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reactivar")
                    }
                } else {
                    OutlinedButton(
                        onClick = onDiscontinue,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Descontinuar")
                    }
                }
            }
        }
    }
}

