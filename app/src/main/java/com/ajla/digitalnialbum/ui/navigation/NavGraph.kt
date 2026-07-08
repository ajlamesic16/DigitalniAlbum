package com.ajla.digitalnialbum.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ajla.digitalnialbum.ui.album.AlbumScreen
import com.ajla.digitalnialbum.ui.detail.StickerDetailScreen
import com.ajla.digitalnialbum.ui.favorites.FavoritesScreen
import com.ajla.digitalnialbum.ui.onboarding.OnboardingScreen
import com.ajla.digitalnialbum.ui.packet.PacketScreen
import com.ajla.digitalnialbum.ui.settings.SettingsScreen
import com.ajla.digitalnialbum.ui.splash.SplashScreen
import com.ajla.digitalnialbum.ui.statistics.StatisticsScreen
import com.ajla.digitalnialbum.ui.trade.TradeScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Album : Screen("album")
    data object Favorites : Screen("favorites")
    data object Packet : Screen("packet")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object Trade : Screen("trade")

    data object StickerDetail : Screen("sticker_detail/{stickerId}") {
        fun createRoute(stickerId: Int) = "sticker_detail/$stickerId"
    }
}

private val bottomNavRoutes = listOf(
    Screen.Album.route,
    Screen.Favorites.route,
    Screen.Statistics.route,
    Screen.Trade.route,
    Screen.Settings.route
)

@Composable
fun DigitalniAlbumNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Album.route,
                        onClick = { navController.navigate(Screen.Album.route) },
                        icon = { Icon(Icons.Filled.Collections, contentDescription = null) },
                        label = { Text("Album") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = { navController.navigate(Screen.Favorites.route) },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text("Favoriti") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Statistics.route,
                        onClick = { navController.navigate(Screen.Statistics.route) },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ShowChart,
                                contentDescription = null
                            )
                        },
                        label = { Text("Statistika") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Trade.route,
                        onClick = { navController.navigate(Screen.Trade.route) },
                        icon = { Icon(Icons.Filled.SwapHoriz, contentDescription = null) },
                        label = { Text("Razmjena") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = { navController.navigate(Screen.Settings.route) },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        label = { Text("Postavke") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(
                if (showBottomBar)
                    padding
                else
                    PaddingValues(0.dp)
            )
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        navController.navigate(Screen.Album.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Album.route) {
                AlbumScreen(
                    onStickerClick = { id ->
                        navController.navigate(Screen.StickerDetail.createRoute(id))
                    },
                    onOpenPacketClick = {
                        navController.navigate(Screen.Packet.route)
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onStickerClick = { id ->
                        navController.navigate(Screen.StickerDetail.createRoute(id))
                    }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(Screen.Packet.route) {
                PacketScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.StickerDetail.route,
                arguments = listOf(
                    navArgument("stickerId") {
                        type = NavType.IntType
                    }
                )
            ) {
                StickerDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(Screen.Trade.route) {
                TradeScreen()
            }
        }
    }
}