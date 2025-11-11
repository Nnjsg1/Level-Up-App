package com.example.level_up_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.level_up_app.ui.screens.CatalogScreen
import com.example.level_up_app.ui.screens.HomeScreen
import com.example.level_up_app.ui.screens.NewsScreen
import com.example.level_up_app.ui.screens.ProfileScreen

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Catalog.route) {
            CatalogScreen()
        }
        composable(BottomNavItem.News.route) {
            NewsScreen()
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}