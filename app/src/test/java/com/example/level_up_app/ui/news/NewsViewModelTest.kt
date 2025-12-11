package com.example.level_up_app.ui.news

import android.util.Log
import com.example.level_up_app.data.News
import com.example.level_up_app.data.NewsRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class NewsViewModelTest {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsRepository: NewsRepository
    private val testDispatcher = StandardTestDispatcher()

    private val mockNews = listOf(
        News(
            id = 1L,
            title = "Noticia Gaming",
            content = "Contenido gaming",
            summary = "Resumen gaming",
            image = "gaming.jpg",
            author = "Admin",
            category = "Gaming",
            views = 100,
            isPublished = true,
            createdAt = "2024-01-01"
        ),
        News(
            id = 2L,
            title = "Noticia Tecnología",
            content = "Contenido tecnología",
            summary = "Resumen tecnología",
            image = "tech.jpg",
            author = "Editor",
            category = "Tecnología",
            views = 50,
            isPublished = true,
            createdAt = "2024-01-02"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        newsRepository = mockk(relaxed = true)
        viewModel = NewsViewModel()

        // Inject mock repository
        val field = NewsViewModel::class.java.getDeclaredField("newsRepository")
        field.isAccessible = true
        field.set(viewModel, newsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadNews debe cargar noticias exitosamente`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews

        // Cuando
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.newsList.size)
    }

    @Test
    fun `loadNews debe manejar error de carga`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns null

        // Cuando
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error al cargar noticias", state.error)
        assertTrue(state.newsList.isEmpty())
    }

    @Test
    fun `loadNews debe manejar excepciones`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } throws Exception("Network error")

        // Cuando
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Error"))
    }

    @Test
    fun `filterByCategory con null debe mostrar todas las noticias`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.filterByCategory(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(2, state.newsList.size)
        assertNull(state.selectedCategory)
    }

    @Test
    fun `filterByCategory debe filtrar por categoria especifica`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews
        coEvery { newsRepository.getNewsByCategory("Gaming") } returns listOf(mockNews[0])
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.filterByCategory("Gaming")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(1, state.newsList.size)
        assertEquals("Gaming", state.newsList[0].category)
        assertEquals("Gaming", state.selectedCategory)
    }

    @Test
    fun `filterByCategory debe manejar categoria sin resultados`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews
        coEvery { newsRepository.getNewsByCategory("Deportes") } returns emptyList()
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.filterByCategory("Deportes")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertTrue(state.newsList.isEmpty())
        assertEquals("Deportes", state.selectedCategory)
    }

    @Test
    fun `searchNews con query vacio debe mostrar todas las noticias`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.searchNews("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(2, state.newsList.size)
    }

    @Test
    fun `searchNews debe buscar en el backend`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews
        coEvery { newsRepository.searchNews("Gaming") } returns listOf(mockNews[0])
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.searchNews("Gaming")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(1, state.newsList.size)
        assertTrue(state.newsList[0].title.contains("Gaming"))
    }

    @Test
    fun `searchNews debe buscar localmente si falla el backend`() = runTest {
        // Dado
        coEvery { newsRepository.fetchPublishedNews() } returns mockNews
        coEvery { newsRepository.searchNews(any()) } returns null
        viewModel.loadNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.searchNews("Gaming")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(1, state.newsList.size)
        assertTrue(state.newsList[0].title.contains("Gaming"))
    }

    @Test
    fun `incrementViews debe llamar al repositorio`() = runTest {
        // Dado
        val newsId = 1L
        coEvery { newsRepository.incrementViews(newsId) } returns true

        // Cuando
        viewModel.incrementViews(newsId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        coVerify { newsRepository.incrementViews(newsId) }
    }
}

