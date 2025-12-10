package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.RetrofitInstance

class ProductRepository {
    private val apiService = RetrofitInstance.api

    /**
     * Obtiene todos los productos - método compatible con el admin
     */
    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Productos obtenidos: ${response.body()?.size}")
                response.body() ?: emptyList()
            } else {
                Log.e("ProductRepository", "Error al obtener productos: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Excepción al obtener productos", e)
            emptyList()
        }
    }

    /**
     * Obtiene todos los productos - método alternativo
     */
    suspend fun fetchProducts(): List<Product>? {
        return getAllProducts().takeIf { it.isNotEmpty() }
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
                Log.d("ProductRepository", "Producto creado exitosamente")
                response.body()
            } else {
                Log.e("ProductRepository", "Error al crear producto: ${response.code()}")
                throw Exception("Error al crear producto: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Excepción al crear producto", e)
            throw e
        }
    }

    /**
     * Actualiza un producto existente - para admin
     */
    suspend fun updateProduct(id: Int, product: Product): Product? {
        return try {
            val response = apiService.updateProduct(id.toLong(), product)
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Producto actualizado exitosamente")
                response.body()
            } else {
                Log.e("ProductRepository", "Error al actualizar producto: ${response.code()}")
                throw Exception("Error al actualizar producto: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Excepción al actualizar producto", e)
            throw e
        }
    }

    /**
     * Elimina un producto - para admin
     */
    suspend fun deleteProduct(id: Int) {
        try {
            val response = apiService.deleteProduct(id.toLong())
            if (response.isSuccessful) {
                Log.d("ProductRepository", "Producto eliminado exitosamente")
            } else {
                Log.e("ProductRepository", "Error al eliminar producto: ${response.code()}")
                throw Exception("Error al eliminar producto: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Excepción al eliminar producto", e)
            throw e
        }
    }

    /**
     * Obtiene todas las categorías
     */
    suspend fun getAllCategories(): List<Category> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("ProductRepository", "Error al obtener categorías: ${response.code()}")
                getMockCategories() // Fallback a categorías mock
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Excepción al obtener categorías", e)
            getMockCategories() // Fallback a categorías mock
        }
    }

    /**
     * Obtiene todas las categorías - método alternativo
     */
    suspend fun fetchCategories(): List<Category>? {
        return getAllCategories().takeIf { it.isNotEmpty() }
    }
}

/**
 * Función temporal para obtener categorías mock hasta que el endpoint esté disponible
 */
fun getMockCategories(): List<Category> {
    return listOf(
        Category(1, "Gaming"),
        Category(2, "Accesorios"),
        Category(3, "Hardware"),
        Category(4, "Software"),
        Category(5, "Periféricos")
    )
}

