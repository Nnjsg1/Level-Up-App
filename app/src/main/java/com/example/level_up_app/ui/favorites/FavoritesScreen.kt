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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.level_up_app.data.FavoritesRepository
import com.example.level_up_app.data.Product
import com.example.level_up_app.ui.catalog.ProductCard
import com.example.level_up_app.ui.catalog.ProductDetailDialog

@Composable
fun FavoritesScreen() {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val favorites = FavoritesRepository.favorites

    Box(modifier = Modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
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
        } else {
            // Lista de favoritos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Tienes ${favorites.size} producto${if (favorites.size != 1) "s" else ""} favorito${if (favorites.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(favorites) { product ->
                    ProductCard(
                        product = product,
                        onClick = { selectedProduct = product }
                    )
                }
            }
        }
    }

    // Mostrar diálogo de detalles del producto cuando se selecciona uno
    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            onDismiss = { selectedProduct = null }
        )
    }
}

