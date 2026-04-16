package com.example.diagassistant.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diagassistant.ui.components.NavItem
import com.example.diagassistant.ui.components.BottomNavBar
import com.example.diagassistant.ui.screens.DtcListScreen
import com.example.diagassistant.ui.screens.LiveScreen
import com.example.diagassistant.ui.screens.PlaceholderScreen
import com.example.diagassistant.ui.theme.DiagPalette

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val selectedTab: NavItem = when {
        currentDestination?.hierarchy?.any { it.route == Route.Live.route } == true -> NavItem.DASHBOARD
        currentDestination?.hierarchy?.any { it.route == Route.Dtcs.route } == true -> NavItem.DTC
        currentDestination?.hierarchy?.any { it.route == Route.Report.route } == true -> NavItem.REPORT
        currentDestination?.hierarchy?.any { it.route == Route.Assistant.route } == true -> NavItem.ASSIST
        else -> NavItem.DASHBOARD
    }

    Scaffold(
        containerColor = DiagPalette.Bg,
        bottomBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(117.dp),
                contentAlignment = androidx.compose.ui.Alignment.BottomCenter
            ) {
                BottomNavBar(
                    selected = selectedTab,
                    onSelect = { item ->
                        val route = when (item) {
                            NavItem.DASHBOARD -> Route.Live.route
                            NavItem.DTC -> Route.Dtcs.route
                            NavItem.REPORT -> Route.Report.route
                            NavItem.ASSIST -> Route.Assistant.route

                        }

                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Live.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(Route.Live.route) { LiveScreen() }
            composable(Route.Dtcs.route) { DtcListScreen() }
            composable(Route.Report.route) { PlaceholderScreen("Home/Report (next)") }
            composable(Route.Assistant.route) { PlaceholderScreen("Assistant (next)") }
        }
    }
}