package com.example.level_up_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = "Catalogo"
                )
             },
            label = { Text("Catalogo") }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Noticias"
                )
             },
            label = { Text("Noticias") }
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onItemSelected(3) },
            icon = { 
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil"
                )
             },
            label = { Text("Perfil") }
        )
    }
}
