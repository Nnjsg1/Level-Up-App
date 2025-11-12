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
import com.example.level_up_app.ui.components.TopNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(
    onProfile: () -> Unit = {}
) {
    var selectedIndex by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {TopNavBar(selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it })},
        bottomBar = { BottomNavBar(selectedIndex = selectedIndex, onItemSelected = { selectedIndex = it }, onProfile = onProfile) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("jeshu weko")
        }
    }
}
