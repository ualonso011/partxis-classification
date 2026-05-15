package com.partxis.clasificacion.ui.screens.partida

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.partxis.clasificacion.domain.model.Jugador
import com.partxis.clasificacion.ui.Strings
import com.partxis.clasificacion.ui.theme.ParchisAmarillo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPartidaScreen(
    clasificacionId: Long,
    onBack: () -> Unit,
    onPartidaSaved: () -> Unit,
    currentLanguage: String,
    viewModel: NuevaPartidaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val s = { key: String -> Strings.get(key, currentLanguage) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            onPartidaSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Partida") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ParchisAmarillo,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            dateFormat.format(Date()),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Text(
                    "Selecciona la posición de cada jugador",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.jugadores, key = { it.id }) { jugador ->
                        PosicionCard(
                            jugador = jugador,
                            posicion = uiState.posiciones[jugador.id] ?: 0,
                            totalJugadores = uiState.jugadores.size,
                            onPosicionChange = { pos ->
                                viewModel.updatePosicion(jugador.id, pos)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.savePartida() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSaving &&
                                    uiState.posiciones.values.all { it > 0 } &&
                                    uiState.posiciones.values.toSet().size == uiState.posiciones.size
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar Partida")
                            }
                        }
                    }
                }
            }
        }

        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK")
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

@Composable
fun PosicionCard(
    jugador: Jugador,
    posicion: Int,
    totalJugadores: Int,
    onPosicionChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(jugador.getColor())
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                jugador.nombre,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = if (posicion == 0) "Posición" else "${posicion}º",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .width(100.dp)
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    for (i in 1..totalJugadores) {
                        DropdownMenuItem(
                            text = { Text("${i}º") },
                            onClick = {
                                onPosicionChange(i)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}