package com.example.level_up_app.ui.news

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.News
import com.example.level_up_app.data.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NewsState(
    val newsList: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String? = null
)

class NewsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewsState())
    val uiState: StateFlow<NewsState> = _uiState.asStateFlow()

    private val newsRepository = NewsRepository()
    private var allNews: List<News> = emptyList()

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val news = newsRepository.fetchPublishedNews()
                if (news != null) {
                    allNews = news
                    _uiState.value = _uiState.value.copy(
                        newsList = news,
                        isLoading = false,
                        error = null
                    )
                    Log.d("NewsViewModel", "Noticias cargadas: ${news.size}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        newsList = emptyList(),
                        isLoading = false,
                        error = "Error al cargar noticias"
                    )
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error loading news: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun filterByCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)

        if (category == null) {
            _uiState.value = _uiState.value.copy(newsList = allNews)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val results = newsRepository.getNewsByCategory(category)
                if (results != null) {
                    _uiState.value = _uiState.value.copy(
                        newsList = results,
                        isLoading = false
                    )
                } else {
                    // Fallback a filtrado local
                    val localResults = allNews.filter { it.category == category }
                    _uiState.value = _uiState.value.copy(
                        newsList = localResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error filtering by category: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun searchNews(query: String) {
        if (query.isEmpty()) {
            _uiState.value = _uiState.value.copy(newsList = allNews)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val results = newsRepository.searchNews(query)
                if (results != null) {
                    _uiState.value = _uiState.value.copy(
                        newsList = results,
                        isLoading = false
                    )
                } else {
                    // Fallback a búsqueda local
                    val localResults = allNews.filter {
                        it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
                    }
                    _uiState.value = _uiState.value.copy(
                        newsList = localResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error searching news: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun incrementViews(newsId: Long) {
        viewModelScope.launch {
            try {
                newsRepository.incrementViews(newsId)
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error incrementing views: ${e.message}")
            }
        }
    }
}

