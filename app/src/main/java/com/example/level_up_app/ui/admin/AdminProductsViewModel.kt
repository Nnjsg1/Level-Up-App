package com.example.level_up_app.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.Category
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminProductsState(
    val productsList: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val productToDelete: Product? = null
)

class AdminProductsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminProductsState())
    val uiState: StateFlow<AdminProductsState> = _uiState.asStateFlow()

    private val productRepository = ProductRepository()

    init {
        loadAllProducts()
        loadCategories()
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val products = productRepository.fetchProducts()
                if (products != null) {
                    _uiState.value = _uiState.value.copy(
                        productsList = products,
                        isLoading = false,
                        error = null
                    )
                    Log.d("AdminProductsViewModel", "Productos cargados: ${products.size}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        productsList = emptyList(),
                        isLoading = false,
                        error = "Error al cargar productos"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error loading products: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = productRepository.fetchCategories()
                if (categories != null) {
                    _uiState.value = _uiState.value.copy(categories = categories)
                    Log.d("AdminProductsViewModel", "Categorías cargadas: ${categories.size}")
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error loading categories: ${e.message}")
            }
        }
    }

    fun showDeleteDialog(product: Product) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            productToDelete = product
        )
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            productToDelete = null
        )
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val success = productRepository.deleteProduct(productId)
                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Producto eliminado correctamente",
                        showDeleteDialog = false,
                        productToDelete = null
                    )
                    // Recargar la lista
                    loadAllProducts()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al eliminar el producto"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error deleting product: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al eliminar: ${e.localizedMessage}"
                )
            }
        }
    }

    fun createProduct(product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val createdProduct = productRepository.createProduct(product)
                if (createdProduct != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Producto creado correctamente"
                    )
                    loadAllProducts()
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al crear el producto"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error creating product: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al crear: ${e.localizedMessage}"
                )
            }
        }
    }

    fun updateProduct(productId: Long, product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val updatedProduct = productRepository.updateProduct(productId, product)
                if (updatedProduct != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Producto actualizado correctamente"
                    )
                    loadAllProducts()
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al actualizar el producto"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error updating product: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            error = null
        )
    }
}

