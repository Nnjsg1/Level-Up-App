package com.example.level_up_app.ui.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class CatalogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogState())
    val uiState: StateFlow<CatalogState> = _uiState.asStateFlow()

    private val productRepository = ProductRepository()
    private var allProducts: List<Product> = emptyList()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val products = productRepository.fetchProducts()
                if (products != null) {
                    // Filtrar productos descontinuados - solo mostrar activos en el catálogo
                    val activeProducts = products.filter { !it.discontinued }
                    allProducts = activeProducts

                    _uiState.value = _uiState.value.copy(
                        products = activeProducts,
                        isLoading = false,
                        error = null
                    )
                    Log.d("CatalogViewModel", "Productos activos cargados: ${activeProducts.size} de ${products.size} totales")
                } else {
                    _uiState.value = _uiState.value.copy(
                        products = emptyList(),
                        isLoading = false,
                        error = "Error al cargar productos"
                    )
                }
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error loading products: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isEmpty()) {
            _uiState.value = _uiState.value.copy(products = allProducts)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val results = productRepository.searchProducts(query)
                if (results != null) {
                    // Filtrar productos descontinuados de los resultados
                    val activeResults = results.filter { !it.discontinued }
                    _uiState.value = _uiState.value.copy(
                        products = activeResults,
                        isLoading = false
                    )
                } else {
                    // Si falla la búsqueda en backend, buscar localmente (ya filtrado por activos)
                    val localResults = allProducts.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                    }
                    _uiState.value = _uiState.value.copy(
                        products = localResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error searching products: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun filterByCategory(categoryId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val results = productRepository.getProductsByCategory(categoryId)
                if (results != null) {
                    // Filtrar productos descontinuados
                    val activeResults = results.filter { !it.discontinued }
                    _uiState.value = _uiState.value.copy(
                        products = activeResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error filtering by category: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

