package com.example.level_up_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.model.Product
import com.example.level_up_app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _productsState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val productsState: StateFlow<UiState<List<Product>>> = _productsState.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            repository.getAllProducts()
                .onSuccess { products ->
                    _productsState.value = UiState.Success(products)
                }
                .onFailure { error ->
                    _productsState.value = UiState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            repository.searchProducts(query)
                .onSuccess { products ->
                    _productsState.value = UiState.Success(products)
                }
                .onFailure { error ->
                    _productsState.value = UiState.Error(error.message ?: "Error desconocido")
                }
        }
    }
}

