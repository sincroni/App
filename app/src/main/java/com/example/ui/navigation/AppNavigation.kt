package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.EmergencyFlowScreen
import com.example.ui.screens.ClaimStatusScreen
import com.example.ui.screens.TowingRequestScreen
import com.example.ui.screens.GpsTrackingScreen

object Routes {
    const val HOME = "home"
    const val EMERGENCY = "emergency"
    const val CLAIM_STATUS = "claim_status"
    const val TOWING = "towing"
    const val GPS_TRACKING = "gps_tracking"
}

@Composable
fun AppNavigation(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onNavigateToEmergency = { navController.navigate(Routes.EMERGENCY) },
                onNavigateToTowing = { navController.navigate(Routes.TOWING) },
                onNavigateToGps = { navController.navigate(Routes.GPS_TRACKING) },
                onNavigateToStatus = { navController.navigate(Routes.CLAIM_STATUS) }
            )
        }
        composable(Routes.EMERGENCY) {
            EmergencyFlowScreen(
                onBack = { navController.popBackStack() },
                onConfirmEmergency = { 
                    navController.navigate(Routes.CLAIM_STATUS) {
                        popUpTo(Routes.HOME)
                    } 
                }
            )
        }
        composable(Routes.CLAIM_STATUS) {
            ClaimStatusScreen(
                onBack = { navController.navigate(Routes.HOME) {
                    popUpTo(0)
                } }
            )
        }
        composable(Routes.TOWING) {
            TowingRequestScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.GPS_TRACKING) {
            GpsTrackingScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
