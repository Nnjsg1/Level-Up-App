package com.example.level_up_app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.Category
import com.example.level_up_app.data.ProductRepository
import com.example.level_up_app.data.getMockCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOperationSuccess: Boolean = false
)

class AdminProductsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private val productRepository = ProductRepository()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val products = productRepository.getAllProducts()
                val categories = getMockCategories() // Usando categorías mock temporalmente
                _uiState.value = _uiState.value.copy(
                    products = products,
                    categories = categories,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                productRepository.createProduct(product)
                loadData() // Recargar la lista
                _uiState.value = _uiState.value.copy(isOperationSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al crear producto: ${e.message}"
                )
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                productRepository.updateProduct(product.id.toInt(), product)
                loadData() // Recargar la lista
                _uiState.value = _uiState.value.copy(isOperationSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar producto: ${e.message}"
                )
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                productRepository.deleteProduct(productId.toInt())
                loadData() // Recargar la lista
                _uiState.value = _uiState.value.copy(isOperationSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al eliminar producto: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearOperationSuccess() {
        _uiState.value = _uiState.value.copy(isOperationSuccess = false)
    }

    fun refreshData() {
        loadData()
    }
}
