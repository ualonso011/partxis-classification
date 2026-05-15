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
import com.partxis.clasificacion.ui.Strings
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
    currentLanguage: String,
    viewModel: ClasificacionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val s = { key: String -> Strings.get(key, currentLanguage) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.clasificacion?.nombre ?: s("clasificacion")) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = s("volver"))
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
                    Icon(Icons.Default.Add, contentDescription = s("nueva_partida"))
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
                    text = { Text(s("jugadores")) },
                    icon = { Icon(Icons.Default.People, null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(s("ranking")) },
                    icon = { Icon(Icons.Default.Leaderboard, null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(s("historial")) },
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
                    newPlayerName = uiState.newPlayerName,
                    newPlayerColor = uiState.newPlayerColor,
                    onNewPlayerNameChange = { viewModel.updateNewPlayerName(it) },
                    onNewPlayerColorChange = { viewModel.updateNewPlayerColor(it) },
                    showAddDialog = uiState.showAddPlayerDialog,
                    showEditDialog = uiState.showEditPlayerDialog,
                    onShowAddDialog = { viewModel.showAddPlayerDialog() },
                    onShowEditDialog = { viewModel.showEditPlayerDialog(it) },
                    onHideDialog = { viewModel.hideAddPlayerDialog() },
                    onConfirmAdd = { viewModel.addPlayer() },
                    onConfirmEdit = { viewModel.updatePlayer() },
                    currentLanguage = currentLanguage
                )
                1 -> RankingTab(ranking = uiState.ranking, currentLanguage = currentLanguage)
                2 -> HistorialTab(
                    partidas = uiState.partidas,
                    onDeletePartida = { viewModel.deletePartida(it) },
                    currentLanguage = currentLanguage
                )
            }
        }
    }

    if (uiState.showAddPlayerDialog) {
        PlayerDialog(
            title = s("anadir") + " " + s("jugadores").lowercase(),
            nombre = uiState.newPlayerName,
            color = uiState.newPlayerColor,
            onNombreChange = { viewModel.updateNewPlayerName(it) },
            onColorChange = { viewModel.updateNewPlayerColor(it) },
            onConfirm = { viewModel.addPlayer() },
            onDismiss = { viewModel.hideAddPlayerDialog() },
            currentLanguage = currentLanguage
        )
    }

    if (uiState.showEditPlayerDialog) {
        PlayerDialog(
            title = s("editar") + " " + s("jugadores").lowercase(),
            nombre = uiState.newPlayerName,
            color = uiState.newPlayerColor,
            onNombreChange = { viewModel.updateNewPlayerName(it) },
            onColorChange = { viewModel.updateNewPlayerColor(it) },
            onConfirm = { viewModel.updatePlayer() },
            onDismiss = { viewModel.hideEditPlayerDialog() },
            currentLanguage = currentLanguage
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
    newPlayerName: String,
    newPlayerColor: String,
    onNewPlayerNameChange: (String) -> Unit,
    onNewPlayerColorChange: (String) -> Unit,
    showAddDialog: Boolean,
    showEditDialog: Boolean,
    onShowAddDialog: () -> Unit,
    onShowEditDialog: (Jugador) -> Unit,
    onHideDialog: () -> Unit,
    onConfirmAdd: () -> Unit,
    onConfirmEdit: () -> Unit,
    currentLanguage: String
) {
    val s = { key: String -> Strings.get(key, currentLanguage) }
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
                Text("${s("jugadores")} (${jugadores.size})", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onAddPlayer) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(s("anadir"))
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
                        Text(s("anade_jugadores"))
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
                            Strings.get("puntuacion_por_posicion", currentLanguage),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            Strings.get("sin_configurar", currentLanguage),
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
    onDelete: () -> Unit,
    currentLanguage: String = "eu"
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val s = { key: String -> Strings.get(key, currentLanguage) }

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
                    contentDescription = s("editar"),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = s("eliminar"),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(Strings.get("eliminar_jugador", "eu")) },
            text = { Text(Strings.get("confirmar_eliminar_jugador", "eu").format(jugador.nombre)) },
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
fun RankingTab(ranking: List<RankingEntry>, currentLanguage: String = "eu") {
    val s = { key: String -> Strings.get(key, currentLanguage) }
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
                Text(s("no_hay_ranking"))
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
    onDeletePartida: (Long) -> Unit,
    currentLanguage: String = "eu"
) {
    val s = { key: String -> Strings.get(key, currentLanguage) }
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
                Text(s("no_hay_partidas"))
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
    onDelete: () -> Unit,
    currentLanguage: String = "eu"
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
                        contentDescription = Strings.get("eliminar", "eu"),
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
            title = { Text(Strings.get("eliminar_partida", "eu")) },
            text = { Text(Strings.get("confirmar_eliminar_partida", "eu")) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(Strings.get("eliminar", "eu"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(Strings.get("cancelar", "eu"))
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
    onDismiss: () -> Unit,
    currentLanguage: String
) {
    val s = { key: String -> Strings.get(key, currentLanguage) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNombreChange,
                    label = { Text(s("nombre")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(s("color"), style = MaterialTheme.typography.labelMedium)

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
                Text(s("guardar"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(s("cancelar"))
            }
        }
    )
}