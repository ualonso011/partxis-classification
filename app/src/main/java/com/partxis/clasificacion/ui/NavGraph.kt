package com.partxis.clasificacion.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.partxis.clasificacion.ui.screens.clasificacion.ClasificacionDetailScreen
import com.partxis.clasificacion.ui.screens.home.HomeScreen
import com.partxis.clasificacion.ui.screens.partida.NuevaPartidaScreen
import com.partxis.clasificacion.ui.screens.home.EditPuntuacionesScreen
import com.partxis.clasificacion.ui.screens.home.VersionInfoScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ClasificacionDetail : Screen("clasificacion/{clasificacionId}") {
        fun createRoute(clasificacionId: Long) = "clasificacion/$clasificacionId"
    }
    object NuevaPartida : Screen("nueva_partida/{clasificacionId}") {
        fun createRoute(clasificacionId: Long) = "nueva_partida/$clasificacionId"
    }
    object EditPuntuaciones : Screen("edit_puntuaciones/{clasificacionId}") {
        fun createRoute(clasificacionId: Long) = "edit_puntuaciones/$clasificacionId"
    }
    object VersionInfo : Screen("version_info")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    toggleTheme: () -> Unit,
    isDarkTheme: Boolean,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    currentVersion: String,
    onVersionClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onClasificacionClick = { id ->
                    navController.navigate(Screen.ClasificacionDetail.createRoute(id))
                },
                onVersionClick = onVersionClick,
                toggleTheme = toggleTheme,
                isDarkTheme = isDarkTheme,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange
            )
        }

        composable(
            route = Screen.ClasificacionDetail.route,
            arguments = listOf(navArgument("clasificacionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clasificacionId = backStackEntry.arguments?.getLong("clasificacionId") ?: 0L
            ClasificacionDetailScreen(
                clasificacionId = clasificacionId,
                onBack = { navController.popBackStack() },
                onNuevaPartida = {
                    navController.navigate(Screen.NuevaPartida.createRoute(clasificacionId))
                },
                onEditPuntuaciones = {
                    navController.navigate(Screen.EditPuntuaciones.createRoute(clasificacionId))
                }
            )
        }

        composable(
            route = Screen.NuevaPartida.route,
            arguments = listOf(navArgument("clasificacionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clasificacionId = backStackEntry.arguments?.getLong("clasificacionId") ?: 0L
            NuevaPartidaScreen(
                clasificacionId = clasificacionId,
                onBack = { navController.popBackStack() },
                onPartidaSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditPuntuaciones.route,
            arguments = listOf(navArgument("clasificacionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val clasificacionId = backStackEntry.arguments?.getLong("clasificacionId") ?: 0L
            EditPuntuacionesScreen(
                clasificacionId = clasificacionId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.VersionInfo.route) {
            VersionInfoScreen(
                onBack = { navController.popBackStack() },
                currentVersion = currentVersion
            )
        }
    }
}