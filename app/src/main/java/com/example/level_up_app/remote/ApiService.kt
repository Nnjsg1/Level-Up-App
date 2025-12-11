package com.example.level_up_app.remote

import com.example.level_up_app.data.LoginRequest
import com.example.level_up_app.data.LoginResponse
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.Category
import com.example.level_up_app.data.Tag
import com.example.level_up_app.data.User
import com.example.level_up_app.data.News
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
    suspend fun getProductById(@Path("id") id: Long): Response<Product>

    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: Long): Response<List<Product>>

    @GET("products/search/{title}")
    suspend fun searchProducts(@Path("title") title: String): Response<List<Product>>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: Product): Response<Product>

    @PATCH("products/{id}/discontinue")
    suspend fun discontinueProduct(@Path("id") id: Long): Response<Product>

    @PATCH("products/{id}/reactivate")
    suspend fun reactivateProduct(@Path("id") id: Long): Response<Product>

    @GET("products/active")
    suspend fun getActiveProducts(): Response<List<Product>>

    @GET("products/discontinued")
    suspend fun getDiscontinuedProducts(): Response<List<Product>>

    // ============ ENDPOINTS DE CATEGORÍAS ============

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    // ============ ENDPOINTS DE NOTICIAS ============

    @GET("news")
    suspend fun getAllNews(): Response<List<News>>

    @GET("news/published")
    suspend fun getPublishedNews(): Response<List<News>>

    @GET("news/{id}")
    suspend fun getNewsById(@Path("id") id: Long): Response<News>

    @GET("news/category/{category}")
    suspend fun getNewsByCategory(@Path("category") category: String): Response<List<News>>

    @GET("news/category/{category}/published")
    suspend fun getPublishedNewsByCategory(@Path("category") category: String): Response<List<News>>

    @GET("news/search/{title}")
    suspend fun searchNews(@Path("title") title: String): Response<List<News>>

    @POST("news")
    suspend fun createNews(@Body news: News): Response<News>

    @PUT("news/{id}")
    suspend fun updateNews(@Path("id") id: Long, @Body news: News): Response<News>

    @DELETE("news/{id}")
    suspend fun deleteNews(@Path("id") id: Long): Response<Unit>

    @POST("news/{id}/view")
    suspend fun incrementNewsViews(@Path("id") id: Long): Response<News>
}

