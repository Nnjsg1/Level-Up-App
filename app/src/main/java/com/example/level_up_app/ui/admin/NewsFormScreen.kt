package com.example.level_up_app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.level_up_app.data.News

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFormScreen(
    newsToEdit: News? = null,
    viewModel: AdminNewsViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(newsToEdit?.title ?: "") }
    var content by remember { mutableStateOf(newsToEdit?.content ?: "") }
    var summary by remember { mutableStateOf(newsToEdit?.summary ?: "") }
    var image by remember { mutableStateOf(newsToEdit?.image ?: "") }
    var thumbnail by remember { mutableStateOf(newsToEdit?.thumbnail ?: "") }
    var author by remember { mutableStateOf(newsToEdit?.author ?: "Admin") }
    var category by remember { mutableStateOf(newsToEdit?.category ?: "General") }
    var isPublished by remember { mutableStateOf(newsToEdit?.isPublished ?: true) }

    var showCategoryMenu by remember { mutableStateOf(false) }
    val categories = listOf("General", "Consolas", "Videojuegos", "Accesorios", "Ofertas", "Guías", "Noticias")

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (newsToEdit == null) "Crear Noticia" else "Editar Noticia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                // Resumen
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Resumen") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !uiState.isLoading
                )

                // Contenido
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10,
                    enabled = !uiState.isLoading
                )

                // Categoría
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        enabled = !uiState.isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // Autor
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                // Imagen
                OutlinedTextField(
                    value = image,
                    onValueChange = { image = it },
                    label = { Text("Ruta de imagen/video") },
                    placeholder = { Text("uploads/imagen.jpg o uploads/video.mp4") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                // Thumbnail
                OutlinedTextField(
                    value = thumbnail,
                    onValueChange = { thumbnail = it },
                    label = { Text("Thumbnail (opcional)") },
                    placeholder = { Text("uploads/thumbnail.jpg") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )

                // Switch publicado/borrador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estado:", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPublished) "Publicado" else "Borrador",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isPublished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = isPublished,
                            onCheckedChange = { isPublished = it },
                            enabled = !uiState.isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val newsData = News(
                                id = newsToEdit?.id ?: 0,
                                title = title,
                                content = content,
                                summary = summary,
                                image = image,
                                thumbnail = thumbnail,
                                author = author,
                                category = category,
                                views = newsToEdit?.views ?: 0,
                                isPublished = isPublished,
                                createdAt = newsToEdit?.createdAt ?: "",
                                updatedAt = ""
                            )

                            if (newsToEdit == null) {
                                viewModel.createNews(newsData) { onBack() }
                            } else {
                                viewModel.updateNews(newsToEdit.id, newsData) { onBack() }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading && title.isNotBlank() && content.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (newsToEdit == null) "Crear" else "Guardar")
                        }
                    }
                }
            }
        }
    }
}

