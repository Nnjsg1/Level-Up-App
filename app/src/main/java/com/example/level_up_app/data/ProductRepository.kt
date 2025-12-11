package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import com.example.level_up_app.remote.RetrofitInstance

class ProductRepository(
    private val apiService: ApiService = RetrofitInstance.api
) {

    /**
     * Obtiene todos los productos
     */
    suspend fun fetchProducts(): List<Product>? {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Productos obtenidos: ${response.body()?.size}")
                response.body()
            } else {
                Log.e("ProductRepository", "Error fetching products: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception fetching products: ${e.message}", e)
            null
        }
    }

    /**
     * Obtiene un producto por ID
     */
    suspend fun getProductById(id: Long): Product? {
        return try {
            val response = apiService.getProductById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProductRepository", "Error getting product: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception getting product: ${e.message}")
            null
        }
    }

    /**
     * Obtiene productos por categoría
     */
    suspend fun getProductsByCategory(categoryId: Long): List<Product>? {
        return try {
            val response = apiService.getProductsByCategory(categoryId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProductRepository", "Error getting products by category: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception getting products by category: ${e.message}")
            null
        }
    }

    /**
     * Busca productos por título
     */
    suspend fun searchProducts(title: String): List<Product>? {
        return try {
            val response = apiService.searchProducts(title)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProductRepository", "Error searching products: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception searching products: ${e.message}")
            null
        }
    }

    /**
     * Crea un nuevo producto
     */
    suspend fun createProduct(product: Product): Product? {
        return try {
            val response = apiService.createProduct(product)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProductRepository", "Error creating product: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception creating product: ${e.message}")
            null
        }
    }

    /**
     * Actualiza un producto existente
     */
    suspend fun updateProduct(id: Long, product: Product): Product? {
        return try {
            val response = apiService.updateProduct(id, product)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProductRepository", "Error updating product: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception updating product: ${e.message}")
            null
        }
    }

    /**
     * Descontinúa un producto (soft delete)
     * Los productos descontinuados se filtrarán automáticamente cuando los usuarios
     * carguen sus carritos y favoritos
     */
    suspend fun discontinueProduct(id: Long): Product? {
        return try {
            val response = apiService.discontinueProduct(id)
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Producto descontinuado: $id")
                Log.d("ProductRepository", "Los productos descontinuados se eliminarán automáticamente de carritos y favoritos cuando los usuarios actualicen")
                response.body()
            } else {
                Log.e("ProductRepository", "Error discontinuing product: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception discontinuing product: ${e.message}")
            null
        }
    }


    /**
     * Reactiva un producto descontinuado
     */
    suspend fun reactivateProduct(id: Long): Product? {
        return try {
            val response = apiService.reactivateProduct(id)
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Producto reactivado: $id")
                response.body()
            } else {
                Log.e("ProductRepository", "Error reactivating product: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception reactivating product: ${e.message}")
            null
        }
    }

    /**
     * Obtiene solo productos activos
     */
    suspend fun fetchActiveProducts(): List<Product>? {
        return try {
            val response = apiService.getActiveProducts()
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Productos activos obtenidos: ${response.body()?.size}")
                response.body()
            } else {
                Log.e("ProductRepository", "Error fetching active products: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception fetching active products: ${e.message}")
            null
        }
    }

    /**
     * Obtiene solo productos descontinuados
     */
    suspend fun fetchDiscontinuedProducts(): List<Product>? {
        return try {
            val response = apiService.getDiscontinuedProducts()
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Productos descontinuados obtenidos: ${response.body()?.size}")
                response.body()
            } else {
                Log.e("ProductRepository", "Error fetching discontinued products: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception fetching discontinued products: ${e.message}")
            null
        }
    }

    /**
     * Obtiene todas las categorías
     */
    suspend fun fetchCategories(): List<Category>? {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ProductRepository", "Error fetching categories: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception fetching categories: ${e.message}")
            null
        }
    }
}

