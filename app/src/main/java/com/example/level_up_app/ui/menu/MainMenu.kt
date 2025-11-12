package com.example.level_up_app.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.level_up_app.ui.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu() {
    var selectedIndex by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Level Up - Principal") }
            )
        },
        bottomBar = { BottomNavBar(selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> Text("El", style = MaterialTheme.typography.headlineMedium)
                1 -> Text("Jesus", style = MaterialTheme.typography.headlineMedium)
                2 -> Text("es", style = MaterialTheme.typography.headlineMedium)
                3 -> Text("Entero Weko", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
