package com.example.level_up_app.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.level_up_app.ui.catalog.CatalogScreen
import com.example.level_up_app.ui.cart.CartScreen
import com.example.level_up_app.ui.components.BottomNavBar
import com.example.level_up_app.ui.profile.ProfileEditScreen
import com.example.level_up_app.ui.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(
    onProfile: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(0) }
    // local state to toggle between viewing profile and editing it
    var isEditing by remember { mutableStateOf(false) }

    val currentScreen = when (selectedIndex) {
        0 -> "Inicio"
        1 -> "Catalogo"
        2 -> "Noticias"
        3 -> "Perfil"
        4 -> "Favoritos"
        5 -> "Carrito"
        else -> ""
    }

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentScreen.uppercase()) },
                actions = {
                    IconButton(onClick = { selectedIndex = 4 }) {
                        Icon(
                            imageVector = if (selectedIndex == 4) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritos"
                        )
                    }
                    IconButton(onClick = { selectedIndex = 5 }) {
                        Icon(
                            imageVector = if (selectedIndex == 5) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                            contentDescription = "Carrito"
                        )
                    }
                }
            )
        },
         // when user selects another tab, make sure we leave edit mode
         bottomBar = { BottomNavBar(selectedIndex = selectedIndex, onItemSelected = {
             selectedIndex = it
             if (it != 3) isEditing = false
         }, onProfile = onProfile) }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                1 -> CatalogScreen()
                5 -> CartScreen()
                3 -> {
                    if (isEditing) {
                        ProfileEditScreen(onSave = { _, _, _ -> /* no-op for now */ }, onBack = { isEditing = false })
                    } else {
                        ProfileScreen(onEditClicked = { isEditing = true })
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
