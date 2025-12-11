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
     * Elimina un producto
     */
    suspend fun deleteProduct(id: Long): Boolean {
        return try {
            val response = apiService.deleteProduct(id)
            if (response.isSuccessful) {
                true
            } else {
                Log.e("ProductRepository", "Error deleting product: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Exception deleting product: ${e.message}")
            false
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

