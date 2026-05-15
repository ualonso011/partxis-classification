package com.partxis.clasificacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.partxis.clasificacion.data.PreferencesManager
import com.partxis.clasificacion.ui.NavGraph
import com.partxis.clasificacion.ui.theme.PartxisClasificacionTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                isDarkTheme = preferencesManager.isDarkTheme.first()
            }

            PartxisClasificacionTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    toggleTheme = {
                        isDarkTheme = !isDarkTheme
                        lifecycleScope.launch {
                            preferencesManager.setDarkTheme(isDarkTheme)
                        }
                    },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}