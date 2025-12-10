package com.example.level_up_app.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.level_up_app.ui.viewmodel.ProductViewModel
import com.example.level_up_app.ui.viewmodel.UiState

@Composable
fun ProductListScreen(viewModel: ProductViewModel = viewModel()) {
    val productsState by viewModel.productsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    when (val state = productsState) {
        is UiState.Idle -> {
            Text("Cargando productos...")
        }
        is UiState.Loading -> {
            CircularProgressIndicator()
        }
        is UiState.Success -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(state.data) { product ->
                    Text("${product.title} - $${product.price}")
                }
            }
        }
        is UiState.Error -> {
            Text("Error: ${state.message}")
        }
    }
}

