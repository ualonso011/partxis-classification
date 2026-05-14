package com.partxis.clasificacion.ui.screens.clasificacion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.partxis.clasificacion.domain.model.*
import com.partxis.clasificacion.ui.theme.*
import androidx.compose.foundation.isSystemInDarkTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.partxis.clasificacion.data.local.entity.PuntuacionPosicionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClasificacionDetailScreen(
    clasificacionId: Long,
    onBack: () -> Unit,
    onNuevaPartida: () -> Unit,
    onEditPuntuaciones: () -> Unit,
    viewModel: ClasificacionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.clasificacion?.nombre ?: "Clasificación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0 && uiState.jugadores.size >= 2) {
                FloatingActionButton(
                    onClick = onNuevaPartida,
                    containerColor = ParchisAmarillo
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva partida")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Jugadores") },
                    icon = { Icon(Icons.Default.People, null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Ranking") },
                    icon = { Icon(Icons.Default.Leaderboard, null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Historial") },
                    icon = { Icon(Icons.Default.History, null) }
                )
            }

            when (selectedTab) {
                0 -> JugadoresTab(
                    jugadores = uiState.jugadores,
                    onAddPlayer = { viewModel.showAddPlayerDialog() },
                    onEditPlayer = { viewModel.showEditPlayerDialog(it) },
                    onDeletePlayer = { viewModel.deletePlayer(it) },
                    onEditPuntuaciones = onEditPuntuaciones,
                    puntuaciones = uiState.puntuaciones
                )
                1 -> RankingTab(ranking = uiState.ranking)
                2 -> HistorialTab(
                    partidas = uiState.partidas,
                    onDeletePartida = { viewModel.deletePartida(it) }
                )
            }
        }
    }

    if (uiState.showAddPlayerDialog) {
        PlayerDialog(
            title = "Añadir Jugador",
            nombre = uiState.newPlayerName,
            color = uiState.newPlayerColor,
            onNombreChange = { viewModel.updateNewPlayerName(it) },
            onColorChange = { viewModel.updateNewPlayerColor(it) },
            onConfirm = { viewModel.addPlayer() },
            onDismiss = { viewModel.hideAddPlayerDialog() }
        )
    }

    if (uiState.showEditPlayerDialog) {
        PlayerDialog(
            title = "Editar Jugador",
            nombre = uiState.newPlayerName,
            color = uiState.newPlayerColor,
            onNombreChange = { viewModel.updateNewPlayerName(it) },
            onColorChange = { viewModel.updateNewPlayerColor(it) },
            onConfirm = { viewModel.updatePlayer() },
            onDismiss = { viewModel.hideEditPlayerDialog() }
        )
    }
}

@Composable
fun JugadoresTab(
    jugadores: List<Jugador>,
    onAddPlayer: () -> Unit,
    onEditPlayer: (Jugador) -> Unit,
    onDeletePlayer: (Jugador) -> Unit,
    onEditPuntuaciones: () -> Unit,
    puntuaciones: List<PuntuacionPosicionEntity>
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Jugadores (${jugadores.size})", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onAddPlayer) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Añadir")
                }
            }
        }

        if (jugadores.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Añade jugadores para empezar")
                    }
                }
            }
        }

        items(jugadores, key = { it.id }) { jugador ->
            JugadorCard(
                jugador = jugador,
                onEdit = { onEditPlayer(jugador) },
                onDelete = { onDeletePlayer(jugador) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditPuntuaciones() },
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
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Puntuación por posición",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            if (puntuaciones.isEmpty()) "Sin configurar" else "${puntuaciones.size} posiciones configuradas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JugadorCard(
    jugador: Jugador,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar jugador") },
            text = { Text("¿Eliminar a ${jugador.nombre}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RankingTab(ranking: List<RankingEntry>) {
    if (ranking.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Leaderboard,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No hay ranking todavía")
                Text(
                    "Juega partidas para ver la clasificación",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(ranking) { index, entry ->
                RankingCard(entry = entry, posicion = index + 1)
            }
        }
    }
}

@Composable
fun RankingCard(entry: RankingEntry, posicion: Int) {
    val backgroundColor = when (posicion) {
        1 -> if (isSystemInDarkTheme()) OroDark else Oro
        2 -> if (isSystemInDarkTheme()) PlataDark else Plata
        3 -> if (isSystemInDarkTheme()) BronceDark else Bronce
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = if (posicion <= 3) Color.Black else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(when (posicion) {
                                1 -> if (isSystemInDarkTheme()) OroDark else Oro
                                2 -> if (isSystemInDarkTheme()) PlataDark else Plata
                                3 -> if (isSystemInDarkTheme()) BronceDark else Bronce
                                else -> MaterialTheme.colorScheme.primary
                            }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$posicion",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (posicion <= 3) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(entry.jugador.getColor())
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.jugador.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${entry.victorias} victorias",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (posicion <= 3) Color.Black.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                "${entry.puntosTotales} pts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (posicion <= 3) Color.Black else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun HistorialTab(
    partidas: List<Partida>,
    onDeletePartida: (Long) -> Unit
) {
    if (partidas.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No hay partidas jugadas")
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(partidas, key = { it.id }) { partida ->
                PartidaCard(
                    partida = partida,
                    onDelete = { onDeletePartida(partida.id) }
                )
            }
        }
    }
}

@Composable
fun PartidaCard(
    partida: Partida,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    dateFormat.format(Date(partida.fecha)),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            partida.resultados.sortedBy { it.posicion }.forEach { resultado ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${resultado.posicion}º",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = when (resultado.posicion) {
                            1 -> if (isSystemInDarkTheme()) OroDark else Oro
                            2 -> if (isSystemInDarkTheme()) PlataDark else Plata
                            3 -> if (isSystemInDarkTheme()) BronceDark else Bronce
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.width(32.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(resultado.jugador.getColor())
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        resultado.jugador.nombre,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        "+${resultado.puntos} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar partida") },
            text = { Text("¿Eliminar esta partida del historial?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PlayerDialog(
    title: String,
    nombre: String,
    color: String,
    onNombreChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Color", style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ColoresJugador.colores) { (nombreColor, hexColor) ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hexColor)))
                                .border(
                                    width = if (color == hexColor) 3.dp else 0.dp,
                                    color = if (color == hexColor) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onColorChange(hexColor) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = nombre.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}