package com.partxis.clasificacion.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.partxis.clasificacion.ui.Strings
import com.partxis.clasificacion.ui.theme.Oro
import com.partxis.clasificacion.ui.theme.Plata
import com.partxis.clasificacion.ui.theme.Bronce
import com.partxis.clasificacion.ui.theme.OroDark
import com.partxis.clasificacion.ui.theme.PlataDark
import com.partxis.clasificacion.ui.theme.BronceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPuntuacionesScreen(
    clasificacionId: Long,
    onBack: () -> Unit,
    currentLanguage: String,
    viewModel: EditPuntuacionesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = { key: String -> Strings.get(key, currentLanguage) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s("puntuacion_por_posicion")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = s("volver"))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        s("configura_puntos"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(uiState.puntuaciones) { puntuacion ->
                    PuntuacionPosicionCard(
                        posicion = puntuacion.posicion,
                        puntos = puntuacion.puntos,
                        onPuntosChange = { puntos ->
                            viewModel.updatePuntuacion(puntuacion.posicion, puntos)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
s("orden_invertido"),

                                    s("orden_invertido_desc"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Switch(
                                checked = uiState.puntuacionInvertida,
                                onCheckedChange = { viewModel.setPuntuacionInvertida(it) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.savePuntuaciones() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s("guardar_puntuaciones"))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuntuacionPosicionCard(
    posicion: Int,
    puntos: Int,
    onPuntosChange: (Int) -> Unit
) {
    val posicionColor = when (posicion) {
        1 -> if (isSystemInDarkTheme()) OroDark else Oro
        2 -> if (isSystemInDarkTheme()) PlataDark else Plata
        3 -> if (isSystemInDarkTheme()) BronceDark else Bronce
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    var textoTemporal by remember(puntos) { mutableStateOf(puntos.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = posicionColor,
            contentColor = if (posicion <= 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${posicion}º",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(50.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                s("puntos"),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = textoTemporal,
                onValueChange = { nuevoTexto ->
                    textoTemporal = nuevoTexto
                    nuevoTexto.toIntOrNull()?.let { nuevoValor ->
                        onPuntosChange(nuevoValor)
                    }
                },
                modifier = Modifier.width(80.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (posicion <= 3) Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (posicion <= 3) Color.Black.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline,
                    focusedTextColor = if (posicion <= 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = if (posicion <= 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}