package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.RetrofitInstance
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryTest {

    private lateinit var repository: NewsRepository
    private lateinit var apiService: com.example.level_up_app.remote.ApiService

    private val mockNews = listOf(
        News(
            id = 1L,
            title = "Noticia 1",
            content = "Contenido de la noticia 1",
            summary = "Resumen 1",
            image = "image1.jpg",
            author = "Admin",
            category = "Tecnología",
            views = 100,
            isPublished = true,
            createdAt = "2024-01-01"
        ),
        News(
            id = 2L,
            title = "Noticia 2",
            content = "Contenido de la noticia 2",
            summary = "Resumen 2",
            image = "image2.jpg",
            author = "Editor",
            category = "Gaming",
            views = 50,
            isPublished = true,
            createdAt = "2024-01-02"
        )
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        mockkObject(RetrofitInstance)
        apiService = mockk(relaxed = true)
        every { RetrofitInstance.api } returns apiService

        repository = NewsRepository()
    }

    @Test
    fun `fetchPublishedNews debe retornar lista de noticias publicadas`() = runTest {
        // Dado
        coEvery { apiService.getPublishedNews() } returns Response.success(mockNews)

        // Cuando
        val result = repository.fetchPublishedNews()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        assertTrue(result?.all { it.isPublished } == true)
        coVerify { apiService.getPublishedNews() }
    }

    @Test
    fun `fetchPublishedNews debe retornar lista vacia cuando hay error`() = runTest {
        // Dado
        coEvery { apiService.getPublishedNews() } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.fetchPublishedNews()

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `fetchPublishedNews debe retornar null cuando hay excepcion`() = runTest {
        // Dado
        coEvery { apiService.getPublishedNews() } throws Exception("Network error")

        // Cuando
        val result = repository.fetchPublishedNews()

        // Entonces
        assertNull(result)
    }

    @Test
    fun `fetchAllNews debe retornar todas las noticias`() = runTest {
        // Dado
        coEvery { apiService.getAllNews() } returns Response.success(mockNews)

        // Cuando
        val result = repository.fetchAllNews()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        coVerify { apiService.getAllNews() }
    }

    @Test
    fun `getNewsById debe retornar noticia por id`() = runTest {
        // Dado
        val newsId = 1L
        val expectedNews = mockNews[0]
        coEvery { apiService.getNewsById(newsId) } returns Response.success(expectedNews)

        // Cuando
        val result = repository.getNewsById(newsId)

        // Entonces
        assertNotNull(result)
        assertEquals(newsId, result?.id)
        assertEquals("Noticia 1", result?.title)
    }

    @Test
    fun `getNewsById debe retornar null cuando no existe`() = runTest {
        // Dado
        val newsId = 999L
        coEvery { apiService.getNewsById(newsId) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.getNewsById(newsId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `getNewsByCategory debe retornar noticias por categoria`() = runTest {
        // Dado
        val category = "Tecnología"
        val filteredNews = mockNews.filter { it.category == category }
        coEvery { apiService.getPublishedNewsByCategory(category) } returns Response.success(filteredNews)

        // Cuando
        val result = repository.getNewsByCategory(category)

        // Entonces
        assertNotNull(result)
        assertEquals(1, result?.size)
        assertTrue(result?.all { it.category == category } == true)
    }

    @Test
    fun `getNewsByCategory debe retornar lista vacia cuando no hay noticias`() = runTest {
        // Dado
        val category = "Deportes"
        coEvery { apiService.getPublishedNewsByCategory(category) } returns Response.success(emptyList())

        // Cuando
        val result = repository.getNewsByCategory(category)

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `searchNews debe retornar resultados de busqueda`() = runTest {
        // Dado
        val query = "Noticia"
        coEvery { apiService.searchNews(query) } returns Response.success(mockNews)

        // Cuando
        val result = repository.searchNews(query)

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
    }

    @Test
    fun `createNews debe crear noticia exitosamente`() = runTest {
        // Dado
        val newNews = mockNews[0].copy(id = 0L)
        coEvery { apiService.createNews(newNews) } returns Response.success(mockNews[0])

        // Cuando
        val result = repository.createNews(newNews)

        // Entonces
        assertNotNull(result)
        assertEquals(1L, result?.id)
        coVerify { apiService.createNews(newNews) }
    }

    @Test
    fun `createNews debe retornar null cuando hay error`() = runTest {
        // Dado
        val newNews = mockNews[0].copy(id = 0L)
        coEvery { apiService.createNews(newNews) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // Cuando
        val result = repository.createNews(newNews)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `updateNews debe actualizar noticia exitosamente`() = runTest {
        // Dado
        val newsId = 1L
        val updatedNews = mockNews[0].copy(title = "Título actualizado")
        coEvery { apiService.updateNews(newsId, updatedNews) } returns Response.success(updatedNews)

        // Cuando
        val result = repository.updateNews(newsId, updatedNews)

        // Entonces
        assertNotNull(result)
        assertEquals("Título actualizado", result?.title)
    }

    @Test
    fun `deleteNews debe eliminar noticia exitosamente`() = runTest {
        // Dado
        val newsId = 1L
        coEvery { apiService.deleteNews(newsId) } returns Response.success(Unit)

        // Cuando
        val result = repository.deleteNews(newsId)

        // Entonces
        assertTrue(result)
        coVerify { apiService.deleteNews(newsId) }
    }

    @Test
    fun `deleteNews debe retornar false cuando hay error`() = runTest {
        // Dado
        val newsId = 1L
        coEvery { apiService.deleteNews(newsId) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.deleteNews(newsId)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `incrementViews debe incrementar vistas exitosamente`() = runTest {
        // Dado
        val newsId = 1L
        val updatedNews = mockNews[0].copy(views = 101)
        coEvery { apiService.incrementNewsViews(newsId) } returns Response.success(updatedNews)

        // Cuando
        val result = repository.incrementViews(newsId)

        // Entonces
        assertNotNull(result)
        assertTrue(result == true)
    }
}
