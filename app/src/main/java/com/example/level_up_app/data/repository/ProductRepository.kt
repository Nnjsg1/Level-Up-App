package com.example.level_up_app.data.repository

import com.example.level_up_app.data.model.Product
import com.example.level_up_app.data.network.RetrofitClient

class ProductRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val response = api.getAllProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = api.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val response = api.searchProducts(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

