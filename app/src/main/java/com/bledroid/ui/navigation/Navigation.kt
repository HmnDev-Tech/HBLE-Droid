package com.bledroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bledroid.ui.BleDroidViewModel
import com.bledroid.ui.screens.*

object Routes {
    const val DASHBOARD = "dashboard"
    const val FAST_PAIR = "fast_pair"
    const val APPLE = "apple"
    const val SAMSUNG = "samsung"
    const val SWIFT_PAIR = "swift_pair"
    const val LOVESPOUSE = "lovespouse"
    const val MIX_ALL = "mix_all"
    const val SPAM_RADAR = "spam_radar"
    const val SETTINGS = "settings"
}

@Composable
fun BleDroidNavHost(
    navController: NavHostController,
    viewModel: BleDroidViewModel,
) {
    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToFastPair = { navController.navigate(Routes.FAST_PAIR) },
                onNavigateToApple = { navController.navigate(Routes.APPLE) },
                onNavigateToSamsung = { navController.navigate(Routes.SAMSUNG) },
                onNavigateToSwiftPair = { navController.navigate(Routes.SWIFT_PAIR) },
                onNavigateToLovespouse = { navController.navigate(Routes.LOVESPOUSE) },
                onNavigateToMixAll = { navController.navigate(Routes.MIX_ALL) },
                onNavigateToSpamRadar = { navController.navigate(Routes.SPAM_RADAR) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.FAST_PAIR) {
            FastPairScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.APPLE) {
            AppleScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SAMSUNG) {
            SamsungScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SWIFT_PAIR) {
            SwiftPairScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.LOVESPOUSE) {
            LovespouseScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.MIX_ALL) {
            MixAllScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SPAM_RADAR) {
            SpamRadarScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
