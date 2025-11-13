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
import com.example.level_up_app.ui.profile.ProfileScreen
import com.example.level_up_app.ui.main.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(
    onProfile: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(0) }

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
         bottomBar = { BottomNavBar(selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it }, onProfile = onProfile) }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> CatalogScreen()
                3 -> ProfileScreen()
                5 -> CartScreen()
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
