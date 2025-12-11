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
    val showDiscontinueDialog: Boolean = false,
    val productToDiscontinue: Product? = null,
    val showDiscontinuedOnly: Boolean = false
)

class AdminProductsViewModel(
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminProductsState())
    val uiState: StateFlow<AdminProductsState> = _uiState.asStateFlow()


    init {
        loadAllProducts()
        loadCategories()
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val products = if (_uiState.value.showDiscontinuedOnly) {
                    productRepository.fetchDiscontinuedProducts()
                } else {
                    productRepository.fetchActiveProducts()
                }

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

    fun toggleFilter() {
        _uiState.value = _uiState.value.copy(
            showDiscontinuedOnly = !_uiState.value.showDiscontinuedOnly
        )
        loadAllProducts()
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

    fun showDiscontinueDialog(product: Product) {
        _uiState.value = _uiState.value.copy(
            showDiscontinueDialog = true,
            productToDiscontinue = product
        )
    }

    fun hideDiscontinueDialog() {
        _uiState.value = _uiState.value.copy(
            showDiscontinueDialog = false,
            productToDiscontinue = null
        )
    }

    fun discontinueProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val product = productRepository.discontinueProduct(productId)
                if (product != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Producto descontinuado correctamente",
                        showDiscontinueDialog = false,
                        productToDiscontinue = null
                    )
                    loadAllProducts()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al descontinuar el producto"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error discontinuing product: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al descontinuar: ${e.localizedMessage}"
                )
            }
        }
    }

    fun reactivateProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val product = productRepository.reactivateProduct(productId)
                if (product != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Producto reactivado correctamente"
                    )
                    loadAllProducts()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al reactivar el producto"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminProductsViewModel", "Error reactivating product: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al reactivar: ${e.localizedMessage}"
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

