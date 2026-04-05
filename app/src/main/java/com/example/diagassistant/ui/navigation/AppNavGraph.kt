package com.example.diagassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diagassistant.ui.screens.PlaceholderScreen
import com.example.diagassistant.ui.screens.LiveScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Live.route
    ) {
        composable(Route.Live.route) { LiveScreen() }
        composable(Route.Dtcs.route) { PlaceholderScreen("DTCs (next)") }
        composable(Route.Report.route) { PlaceholderScreen("Report (next)") }
        composable(Route.Assistant.route) { PlaceholderScreen("Assistant (next)") }
    }
}