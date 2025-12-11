package com.example.level_up_app.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.level_up_app.data.CartItem
import com.example.level_up_app.ui.catalog.formatPrice
import com.example.level_up_app.utils.ImageUtils
import com.example.level_up_app.utils.SessionManager

@Composable
fun CartScreen(
    viewModel: CartViewModel = remember { CartViewModel() },
    onNavigateToPay: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUser = sessionManager.getUser()

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showClearDialog by remember { mutableStateOf(false) }

    // Cargar carrito al inicio
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            viewModel.loadCart(userId)
        }
    }

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.cartItems.isEmpty() -> {
                    // Carrito vacío
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito vacío",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tu carrito está vacío",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agrega productos desde el catálogo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    // Lista de productos en el carrito
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${uiState.cartItems.size} producto${if (uiState.cartItems.size != 1) "s" else ""} en el carrito",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                TextButton(
                                    onClick = { showClearDialog = true }
                                ) {
                                    Text("Vaciar carrito")
                                }
                            }
                        }

                        items(uiState.cartItems) { cartItem ->
                            currentUser?.id?.let { userId ->
                                CartItemCard(
                                    cartItem = cartItem,
                                    onIncrement = {
                                        viewModel.incrementQuantity(userId, cartItem)
                                    },
                                    onDecrement = {
                                        viewModel.decrementQuantity(userId, cartItem)
                                    },
                                    onRemove = {
                                        viewModel.removeFromCart(userId, cartItem.product.id)
                                    }
                                )
                            }
                        }
                    }

                    // Resumen del total
                    CartSummary(
                        total = uiState.total,
                        itemCount = uiState.cartItems.size,
                        onCheckout = {
                            onNavigateToPay()
                        }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para vaciar carrito
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Vaciar Carrito") },
            text = { Text("¿Estás seguro que deseas eliminar todos los productos del carrito?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentUser?.id?.let { userId ->
                            viewModel.clearCart(userId)
                        }
                        showClearDialog = false
                    }
                ) {
                    Text("Sí, vaciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
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
            if (cartItem.product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageUtils.getImageUrl(cartItem.product.imageUrl),
                    contentDescription = cartItem.product.name,
                    modifier = Modifier
                        .size(80.dp)
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
                    text = cartItem.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "$${formatPrice(cartItem.product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón disminuir
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Disminuir cantidad",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Cantidad
                    Text(
                        text = "${cartItem.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Botón aumentar
                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Aumentar cantidad",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Botón eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CartSummary(
    total: Double,
    itemCount: Int,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal ($itemCount items)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$${formatPrice(total)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                enabled = total > 0
            ) {
                Text("Proceder al Pago")
            }
        }
    }
}

