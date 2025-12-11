package com.example.level_up_app.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.level_up_app.data.Product
import com.example.level_up_app.ui.catalog.ProductCard
import com.example.level_up_app.ui.catalog.ProductDetailDialog
import com.example.level_up_app.utils.SessionManager

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = remember { FavoritesViewModel() }
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUser = sessionManager.getUser()

    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar favoritos al iniciar
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            viewModel.loadFavorites(userId)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.favoriteProducts.isEmpty() -> {
                    // Pantalla vacía cuando no hay favoritos
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Sin favoritos",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes productos favoritos",
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
                    // Lista de favoritos
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Tienes ${uiState.favoriteProducts.size} producto${if (uiState.favoriteProducts.size != 1) "s" else ""} favorito${if (uiState.favoriteProducts.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        items(uiState.favoriteProducts) { product ->
                            ProductCard(
                                product = product,
                                onClick = { selectedProduct = product }
                            )
                        }
                    }
                }
            }
        }

        // Mostrar diálogo de detalles del producto cuando se selecciona uno
        selectedProduct?.let { product ->
            ProductDetailDialog(
                product = product,
                onDismiss = { selectedProduct = null },
                favoritesViewModel = viewModel
            )
        }
    }
}

