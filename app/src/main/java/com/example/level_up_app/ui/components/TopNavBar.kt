package com.example.level_up_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TopNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
){
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 4,
            onClick = { onItemSelected(4) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritos"
                )
            },
            label = { Text("Favoritos") }
        )
        NavigationBarItem(
            selected = selectedIndex == 5,
            onClick = { onItemSelected(5) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Carrito"
                )
            },
            label = { Text("Carrito") }
        )
    }
}
