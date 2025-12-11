package com.example.level_up_app.ui.admin

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.level_up_app.data.News
import com.example.level_up_app.data.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AdminNewsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var newsRepository: NewsRepository
    private lateinit var viewModel: AdminNewsViewModel

    // Datos de prueba
    private val mockNewsList = listOf(
        News(
            id = 1,
            title = "Noticia 1",
            content = "Contenido 1",
            summary = "Resumen 1",
            image = "image1.jpg",
            thumbnail = "thumb1.jpg",
            author = "Autor 1",
            category = "Categoría 1",
            views = 100,
            isPublished = true,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        ),
        News(
            id = 2,
            title = "Noticia 2",
            content = "Contenido 2",
            summary = "Resumen 2",
            image = "image2.jpg",
            thumbnail = "thumb2.jpg",
            author = "Autor 2",
            category = "Categoría 2",
            views = 50,
            isPublished = false,
            createdAt = "2025-01-02",
            updatedAt = "2025-01-02"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        newsRepository = mockk(relaxed = true)

        // Configure mocks for init block
        coEvery { newsRepository.fetchAllNews() } returns emptyList()

        viewModel = AdminNewsViewModel(newsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargar noticias debe actualizar estado con lista de noticias exitosamente`() = runTest {
        // Dado
        coEvery { newsRepository.fetchAllNews() } returns mockNewsList

        // Cuando
        viewModel.loadAllNews()
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.newsList.size)
        assertEquals("Noticia 1", state.newsList[0].title)
        assertNull(state.error)
    }

    @Test
    fun `cargar noticias debe mostrar error cuando falla`() = runTest {
        // Dado
        coEvery { newsRepository.fetchAllNews() } returns null

        // Cuando
        viewModel.loadAllNews()
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.newsList.isEmpty())
        assertEquals("Error al cargar noticias", state.error)
    }

    @Test
    fun `eliminar noticia debe removerla y mostrar mensaje de exito`() = runTest {
        // Dado
        val newsId = 1L
        coEvery { newsRepository.deleteNews(newsId) } returns true
        coEvery { newsRepository.fetchAllNews() } returns emptyList()

        // Cuando
        viewModel.deleteNews(newsId)
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Noticia eliminada correctamente", state.successMessage)
        assertFalse(state.showDeleteDialog)
        assertNull(state.newsToDelete)
        coVerify { newsRepository.deleteNews(newsId) }
    }

    @Test
    fun `mostrar dialogo de confirmacion debe actualizar estado correctamente`() {
        // Dado
        val news = mockNewsList[0]

        // Cuando
        viewModel.showDeleteDialog(news)

        // Entonces
        val state = viewModel.uiState.value
        assertTrue(state.showDeleteDialog)
        assertEquals(news, state.newsToDelete)
    }

    @Test
    fun `ocultar dialogo de confirmacion debe limpiar el estado`() {
        // Dado
        viewModel.showDeleteDialog(mockNewsList[0])

        // Cuando
        viewModel.hideDeleteDialog()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.showDeleteDialog)
        assertNull(state.newsToDelete)
    }

    @Test
    fun `crear noticia debe crearla y llamar al callback de exito`() = runTest {
        // Dado
        val newNews = mockNewsList[0]
        var onSuccessCalled = false
        coEvery { newsRepository.createNews(any()) } returns newNews
        coEvery { newsRepository.fetchAllNews() } returns listOf(newNews)

        // Cuando
        viewModel.createNews(newNews) { onSuccessCalled = true }
        advanceUntilIdle()

        // Entonces
        assertTrue(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Noticia creada correctamente", state.successMessage)
        coVerify { newsRepository.createNews(newNews) }
    }

    @Test
    fun `actualizar noticia debe actualizarla y llamar al callback de exito`() = runTest {
        // Dado
        val newsId = 1L
        val updatedNews = mockNewsList[0].copy(title = "Título actualizado")
        var onSuccessCalled = false
        coEvery { newsRepository.updateNews(newsId, any()) } returns updatedNews
        coEvery { newsRepository.fetchAllNews() } returns listOf(updatedNews)

        // Cuando
        viewModel.updateNews(newsId, updatedNews) { onSuccessCalled = true }
        advanceUntilIdle()

        // Entonces
        assertTrue(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Noticia actualizada correctamente", state.successMessage)
        coVerify { newsRepository.updateNews(newsId, updatedNews) }
    }

    @Test
    fun `limpiar mensajes debe eliminar mensajes de exito y error`() {
        // Dado
        // Simular que hay mensajes
        val field = AdminNewsViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<AdminNewsState>
        stateFlow.value = AdminNewsState(
            successMessage = "Success",
            error = "Error"
        )

        // Cuando
        viewModel.clearMessages()

        // Entonces
        val state = viewModel.uiState.value
        assertNull(state.successMessage)
        assertNull(state.error)
    }
}

