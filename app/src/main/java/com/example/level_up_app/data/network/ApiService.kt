package com.example.level_up_app.data.network

import com.example.level_up_app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // USUARIOS
    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    @GET("users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<User>

    @POST("users")
    suspend fun createUser(@Body user: User): Response<User>

    @POST("users")
    suspend fun createUserWithRequest(@Body request: CreateUserRequest): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<User>

    @PATCH("users/{id}/deactivate")
    suspend fun deactivateUser(@Path("id") id: Int): Response<User>

    @PATCH("users/{id}/activate")
    suspend fun activateUser(@Path("id") id: Int): Response<User>

    @GET("users/active")
    suspend fun getActiveUsers(): Response<List<User>>

    @GET("users/inactive")
    suspend fun getInactiveUsers(): Response<List<User>>

    // PRODUCTOS
    @GET("products")
    suspend fun getAllProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<Product>

    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: Int): Response<List<Product>>

    @GET("products/search/{title}")
    suspend fun searchProducts(@Path("title") title: String): Response<List<Product>>

    @POST("products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Response<Product>

    @PATCH("products/{id}/discontinue")
    suspend fun discontinueProduct(@Path("id") id: Int): Response<Product>

    @PATCH("products/{id}/reactivate")
    suspend fun reactivateProduct(@Path("id") id: Int): Response<Product>

    @GET("products/active")
    suspend fun getActiveProducts(): Response<List<Product>>

    @GET("products/discontinued")
    suspend fun getDiscontinuedProducts(): Response<List<Product>>

    // CATEGORÍAS
    @GET("categories")
    suspend fun getAllCategories(): Response<List<Category>>

    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Response<Category>

    @POST("categories")
    suspend fun createCategory(@Body category: Category): Response<Category>

    // ÓRDENES
    @GET("orders")
    suspend fun getAllOrders(): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<Order>

    @GET("orders/user/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Int): Response<List<Order>>

    @POST("orders")
    suspend fun createOrder(@Body order: Order): Response<Order>

    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") id: Int, @Body order: Order): Response<Order>

    // FAVORITOS
    @GET("favorites/user/{userId}")
    suspend fun getFavoritesByUser(@Path("userId") userId: Int): Response<List<Favorite>>

    @POST("favorites")
    suspend fun addFavorite(@Body favorite: Favorite): Response<Favorite>

    @DELETE("favorites/{userId}/{productId}")
    suspend fun removeFavorite(
        @Path("userId") userId: Int,
        @Path("productId") productId: Int
    ): Response<Unit>
}

