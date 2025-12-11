package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

class FavoritesRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: FavoritesRepository

    private val mockFavorites = listOf(
        Favorite(userId = 1, productId = 1L, addedAt = "2024-01-01"),
        Favorite(userId = 1, productId = 2L, addedAt = "2024-01-02")
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        apiService = mockk(relaxed = true)
        repository = FavoritesRepository(apiService)
    }

    @Test
    fun `getFavoritesByUser debe retornar lista de favoritos exitosamente`() = runTest {
        // Dado
        val userId = 1L
        coEvery { apiService.getFavoritesByUser(userId.toInt()) } returns Response.success(mockFavorites)

        // Cuando
        val result = repository.getFavoritesByUser(userId)

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        coVerify { apiService.getFavoritesByUser(userId.toInt()) }
    }

    @Test
    fun `getFavoritesByUser debe retornar lista vacia cuando hay error`() = runTest {
        // Dado
        val userId = 1L
        coEvery { apiService.getFavoritesByUser(userId.toInt()) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.getFavoritesByUser(userId)

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `getFavoritesByUser debe retornar null cuando hay excepcion`() = runTest {
        // Dado
        val userId = 1L
        coEvery { apiService.getFavoritesByUser(userId.toInt()) } throws Exception("Network error")

        // Cuando
        val result = repository.getFavoritesByUser(userId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `addToFavorites debe agregar producto exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val productId = 5L
        val expectedFavorite = Favorite(userId = userId.toInt(), productId = productId)

        coEvery { apiService.createFavorite(any()) } returns Response.success(expectedFavorite)

        // Cuando
        val result = repository.addToFavorites(userId, productId)

        // Entonces
        assertNotNull(result)
        assertEquals(productId, result?.productId)
        coVerify { apiService.createFavorite(any()) }
    }

    @Test
    fun `addToFavorites debe retornar null cuando hay error`() = runTest {
        // Dado
        val userId = 1L
        val productId = 5L

        coEvery { apiService.createFavorite(any()) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // Cuando
        val result = repository.addToFavorites(userId, productId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `addToFavorites debe manejar excepciones`() = runTest {
        // Dado
        val userId = 1L
        val productId = 5L

        coEvery { apiService.createFavorite(any()) } throws Exception("Network error")

        // Cuando
        val result = repository.addToFavorites(userId, productId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `removeFromFavorites debe eliminar favorito exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val productId = 5L

        coEvery { apiService.deleteFavorite(userId.toInt(), productId) } returns Response.success(Unit)

        // Cuando
        val result = repository.removeFromFavorites(userId, productId)

        // Entonces
        assertTrue(result)
        coVerify { apiService.deleteFavorite(userId.toInt(), productId) }
    }

    @Test
    fun `removeFromFavorites debe retornar false cuando hay error`() = runTest {
        // Dado
        val userId = 1L
        val productId = 5L

        coEvery { apiService.deleteFavorite(userId.toInt(), productId) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.removeFromFavorites(userId, productId)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `removeFromFavorites debe manejar excepciones`() = runTest {
        // Dado
        val userId = 1L
        val productId = 5L

        coEvery { apiService.deleteFavorite(userId.toInt(), productId) } throws Exception("Network error")

        // Cuando
        val result = repository.removeFromFavorites(userId, productId)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `getAllFavorites debe retornar todos los favoritos exitosamente`() = runTest {
        // Dado
        coEvery { apiService.getAllFavorites() } returns Response.success(mockFavorites)

        // Cuando
        val result = repository.getAllFavorites()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        coVerify { apiService.getAllFavorites() }
    }

    @Test
    fun `getAllFavorites debe manejar errores`() = runTest {
        // Dado
        coEvery { apiService.getAllFavorites() } returns Response.error(
            500,
            "Server error".toResponseBody()
        )

        // Cuando
        val result = repository.getAllFavorites()

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }
}

