package com.example.level_up_app.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.News
import com.example.level_up_app.data.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminNewsState(
    val newsList: List<News> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val newsToDelete: News? = null
)

class AdminNewsViewModel(
    private val newsRepository: NewsRepository = NewsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminNewsState())
    val uiState: StateFlow<AdminNewsState> = _uiState.asStateFlow()


    init {
        loadAllNews()
    }

    fun loadAllNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val news = newsRepository.fetchAllNews() // Incluye borradores
                if (news != null) {
                    _uiState.value = _uiState.value.copy(
                        newsList = news,
                        isLoading = false,
                        error = null
                    )
                    Log.d("AdminNewsViewModel", "Noticias cargadas: ${news.size}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        newsList = emptyList(),
                        isLoading = false,
                        error = "Error al cargar noticias"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminNewsViewModel", "Error loading news: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun showDeleteDialog(news: News) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            newsToDelete = news
        )
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            newsToDelete = null
        )
    }

    fun deleteNews(newsId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val success = newsRepository.deleteNews(newsId)
                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Noticia eliminada correctamente",
                        showDeleteDialog = false,
                        newsToDelete = null
                    )
                    // Recargar la lista
                    loadAllNews()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al eliminar la noticia"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminNewsViewModel", "Error deleting news: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al eliminar: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            error = null
        )
    }

    fun createNews(news: News, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val createdNews = newsRepository.createNews(news)
                if (createdNews != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Noticia creada correctamente"
                    )
                    loadAllNews()
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al crear la noticia"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminNewsViewModel", "Error creating news: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al crear: ${e.localizedMessage}"
                )
            }
        }
    }

    fun updateNews(newsId: Long, news: News, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val updatedNews = newsRepository.updateNews(newsId, news)
                if (updatedNews != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Noticia actualizada correctamente"
                    )
                    loadAllNews()
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al actualizar la noticia"
                    )
                }
            } catch (e: Exception) {
                Log.e("AdminNewsViewModel", "Error updating news: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al actualizar: ${e.localizedMessage}"
                )
            }
        }
    }
}

