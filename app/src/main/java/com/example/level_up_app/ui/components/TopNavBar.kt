package com.example.level_up_app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun TopNavBar(
    isAdmin: Boolean = false,
    onAdminProductsClick: () -> Unit = {},
    onAdminUsersClick: () -> Unit = {},
    onAdminNewsClick: () -> Unit = {},
    onFavoriteClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var showAdminMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Menú de administrador (lado izquierdo)
        if (isAdmin) {
            IconButton(onClick = { showAdminMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.AdminPanelSettings,
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
                        onAdminProductsClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Administrar Usuarios") },
                    onClick = {
                        showAdminMenu = false
                        onAdminUsersClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Administrar Noticias") },
                    onClick = {
                        showAdminMenu = false
                        onAdminNewsClick()
                    }
                )
            }
        }

        // Iconos de favoritos y carrito (lado derecho)
        Row {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritos"
                )
            }
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Carrito"
                )
            }
        }
    }
}
