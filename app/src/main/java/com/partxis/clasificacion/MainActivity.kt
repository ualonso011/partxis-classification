package com.partxis.clasificacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.partxis.clasificacion.ui.NavGraph
import com.partxis.clasificacion.ui.theme.PartxisClasificacionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            PartxisClasificacionTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    toggleTheme = { isDarkTheme = !isDarkTheme },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}