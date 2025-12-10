package com.example.level_up_app.remote

import com.example.level_up_app.data.LoginRequest
import com.example.level_up_app.data.LoginResponse
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============ ENDPOINTS DE AUTENTICACIÓN ============

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body user: User): Response<LoginResponse>

    // ============ ENDPOINTS DE USUARIOS ============

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    // ============ ENDPOINTS DE PRODUCTOS ============

    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<Product>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Response<Product>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Unit>
}

