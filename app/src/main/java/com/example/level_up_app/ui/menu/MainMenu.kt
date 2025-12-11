package com.example.level_up_app.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.example.level_up_app.ui.catalog.CatalogScreen
import com.example.level_up_app.ui.cart.CartScreen
import com.example.level_up_app.ui.components.BottomNavBar
import com.example.level_up_app.ui.news.NewsScreen
import com.example.level_up_app.ui.admin.AdminNewsScreen
import com.example.level_up_app.ui.admin.NewsFormScreen
import com.example.level_up_app.ui.admin.AdminNewsViewModel
import com.example.level_up_app.ui.admin.AdminProductsScreen
import com.example.level_up_app.ui.profile.ProfileEditScreen
import com.example.level_up_app.ui.profile.ProfileScreen
import com.example.level_up_app.ui.main.HomeScreen
import com.example.level_up_app.ui.favorites.FavoritesScreen
import com.example.level_up_app.buys.PayScreen
import com.example.level_up_app.buys.PayResultScreen
import com.example.level_up_app.screen.Fondo_2
import androidx.compose.ui.platform.LocalContext
import com.example.level_up_app.utils.SessionManager
import com.example.level_up_app.ui.screen.UserAdminScreen

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(
    onProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var currentUser by remember { mutableStateOf(sessionManager.getUser()) }

    var selectedIndex by remember { mutableStateOf(0) }
    var isEditing by remember { mutableStateOf(false) }
    // nuevo estado: resultado del último pago (null = aún no se ha hecho)
    var lastPaymentSuccess by remember { mutableStateOf<Boolean?>(null) }
    // Estado para controlar el menú desplegable de administrador
    var showAdminMenu by remember { mutableStateOf(false) }
    // Estado para controlar el diálogo de cerrar sesión
    var showLogoutDialog by remember { mutableStateOf(false) }
    // Estado para controlar la navegación a administración de usuarios
    var showUserAdmin by remember { mutableStateOf(false) }
    // Estado para controlar la navegación a admin de productos
    var showAdminProducts by remember { mutableStateOf(false) }
    // Estado para crear/editar noticias
    var newsToEdit by remember { mutableStateOf<com.example.level_up_app.data.News?>(null) }
    var showNewsForm by remember { mutableStateOf(false) }

    val currentScreen = when (selectedIndex) {
        0 -> "Inicio"
        1 -> "Catalogo"
        2 -> "Noticias"
        3 -> "Perfil"
        4 -> "Favoritos"
        5 -> "Carrito"
        6 -> "Pago"
        7 -> "Resultado pago"
        else -> ""
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentScreen.uppercase()) },
                navigationIcon = {
                    // Menú de administrador (solo si es admin)
                    if (currentUser?.isAdmin == true) {

                        IconButton(onClick = { showAdminMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Panel de Administración"
                            )
                        }

                        DropdownMenu(
                            expanded = showAdminMenu,
                            onDismissRequest = { showAdminMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Administrar Productos") },
                                onClick = {
                                    showAdminMenu = false
                                    // Resetear otros estados de admin
                                    showUserAdmin = false
                                    showNewsForm = false
                                    selectedIndex = 0
                                    // Activar administrar productos
                                    showAdminProducts = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Administrar Usuarios") },
                                onClick = {
                                    showAdminMenu = false
                                    // Resetear otros estados de admin
                                    showAdminProducts = false
                                    showNewsForm = false
                                    selectedIndex = 0
                                    // Activar administrar usuarios
                                    showUserAdmin = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Administrar Noticias") },
                                onClick = {
                                    showAdminMenu = false
                                    // Resetear otros estados de admin
                                    showUserAdmin = false
                                    showAdminProducts = false
                                    // Activar administrar noticias
                                    selectedIndex = 8
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        selectedIndex = 4
                        showUserAdmin = false
                        showAdminProducts = false
                        showNewsForm = false
                    }) {
                        Icon(
                            imageVector = if (selectedIndex == 4) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritos"
                        )
                    }
                    IconButton(onClick = {
                        selectedIndex = 5
                        showUserAdmin = false
                        showAdminProducts = false
                        showNewsForm = false
                    }) {
                        Icon(
                            imageVector = if (selectedIndex == 5) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },
         // when user selects another tab, make sure we leave edit mode
         bottomBar = { BottomNavBar(selectedIndex = selectedIndex, onItemSelected = { index ->
             selectedIndex = index
             if (index != 3) isEditing = false
             // Resetear estados de pantallas de administración al cambiar de tab
             showUserAdmin = false
             showAdminProducts = false
             showNewsForm = false
         }, onProfile = onProfile) }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Pantalla de administración de usuarios
                showUserAdmin -> {
                    UserAdminScreen(
                        onNavigateBack = {
                            showUserAdmin = false
                            selectedIndex = 0
                        }
                    )
                }
                // Pantalla de administración de productos
                showAdminProducts -> {
                    AdminProductsScreen(
                        onBack = {
                            showAdminProducts = false
                            selectedIndex = 0
                        }
                    )
                }
                // Pantallas normales
                else -> {
                    Fondo_2()
                    when (selectedIndex) {
                        0 -> HomeScreen()
                        1 -> CatalogScreen()
                        2 -> NewsScreen()
                        4 -> FavoritesScreen()
                        5 -> CartScreen(onNavigateToPay = { selectedIndex = 6 })
                        6 -> PayScreen(
                            onCancel = { selectedIndex = 0 },
                            onSuccess = {
                                lastPaymentSuccess = true
                                selectedIndex = 7
                            },
                            onFailure = {
                                lastPaymentSuccess = false
                                selectedIndex = 7
                            }
                        )
                        7 -> PayResultScreen(
                            isSuccess = (lastPaymentSuccess == true),
                            onRetry = {
                                lastPaymentSuccess = null
                                selectedIndex = 6
                            },
                            onGoHome = {
                                lastPaymentSuccess = null
                                selectedIndex = 0
                            }
                        )
                        8 -> {
                            val adminNewsViewModel = remember { AdminNewsViewModel() }

                            if (showNewsForm) {
                                NewsFormScreen(
                                    newsToEdit = newsToEdit,
                                    viewModel = adminNewsViewModel,
                                    onBack = {
                                        showNewsForm = false
                                        newsToEdit = null
                                    }
                                )
                            } else {
                                AdminNewsScreen(
                                    onBack = { selectedIndex = 0 },
                                    onEditNews = { news ->
                                        newsToEdit = news
                                        showNewsForm = true
                                    },
                                    onCreateNews = {
                                        newsToEdit = null
                                        showNewsForm = true
                                    }
                                )
                            }
                        }
                        3 -> {
                            if (isEditing) {
                                ProfileEditScreen(
                                    onSave = { _, _, _ -> /* no-op for now */ },
                                    onBack = {
                                        isEditing = false
                                        currentUser = sessionManager.getUser()
                                    }
                                )
                            } else {
                                ProfileScreen(
                                    name = currentUser?.name ?: "",
                                    email = currentUser?.email ?: "",
                                    onEditClicked = { isEditing = true }
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Pantalla: $currentScreen")
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación de cierre de sesión
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro que deseas cerrar sesión?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            sessionManager.clearSession()
                            onLogout()
                        }
                    ) {
                        Text("Sí, cerrar sesión")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
