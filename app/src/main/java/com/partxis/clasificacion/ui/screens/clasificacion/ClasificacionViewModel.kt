package com.partxis.clasificacion.ui.screens.clasificacion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partxis.clasificacion.data.repository.PartxisRepository
import com.partxis.clasificacion.domain.model.*
import com.partxis.clasificacion.data.local.entity.PuntuacionPosicionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClasificacionDetailUiState(
    val clasificacion: Clasificacion? = null,
    val jugadores: List<Jugador> = emptyList(),
    val ranking: List<RankingEntry> = emptyList(),
    val partidas: List<Partida> = emptyList(),
    val puntuaciones: List<PuntuacionPosicionEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showAddPlayerDialog: Boolean = false,
    val showEditPlayerDialog: Boolean = false,
    val selectedPlayer: Jugador? = null,
    val newPlayerName: String = "",
    val newPlayerColor: String = "#E53935"
)

@HiltViewModel
class ClasificacionViewModel @Inject constructor(
    private val repository: PartxisRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val clasificacionId: Long = savedStateHandle.get<Long>("clasificacionId") ?: 0L

    private val _uiState = MutableStateFlow(ClasificacionDetailUiState())
    val uiState: StateFlow<ClasificacionDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
        viewModelScope.launch {
            repository.partidaSaved.collect {
                loadRanking()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val clasificacion = repository.getClasificacionById(clasificacionId)
            _uiState.value = _uiState.value.copy(clasificacion = clasificacion)
        }

        viewModelScope.launch {
            repository.getJugadoresByClasificacion(clasificacionId).collect { jugadores ->
                _uiState.value = _uiState.value.copy(jugadores = jugadores)
                loadRanking()
            }
        }

        viewModelScope.launch {
            repository.getPartidasByClasificacion(clasificacionId).collect { partidas ->
                _uiState.value = _uiState.value.copy(
                    partidas = partidas,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            repository.getPuntuacionesByClasificacion(clasificacionId).collect { puntuaciones ->
                _uiState.value = _uiState.value.copy(puntuaciones = puntuaciones)
            }
        }
    }

    private fun loadRanking() {
        viewModelScope.launch {
            val invertido = _uiState.value.clasificacion?.puntuacionInvertida ?: false
            val ranking = repository.getRanking(clasificacionId, invertido)
            _uiState.value = _uiState.value.copy(ranking = ranking)
        }
    }

    fun showAddPlayerDialog() {
        _uiState.value = _uiState.value.copy(
            showAddPlayerDialog = true,
            newPlayerName = "",
            newPlayerColor = ColoresJugador.colores.first().second
        )
    }

    fun hideAddPlayerDialog() {
        _uiState.value = _uiState.value.copy(
            showAddPlayerDialog = false,
            newPlayerName = "",
            newPlayerColor = "#E53935"
        )
    }

    fun updateNewPlayerName(name: String) {
        _uiState.value = _uiState.value.copy(newPlayerName = name)
    }

    fun updateNewPlayerColor(color: String) {
        _uiState.value = _uiState.value.copy(newPlayerColor = color)
    }

    fun addPlayer() {
        val nombre = _uiState.value.newPlayerName.trim()
        val color = _uiState.value.newPlayerColor
        if (nombre.isNotEmpty()) {
            viewModelScope.launch {
                repository.addJugador(nombre, color, clasificacionId)
                hideAddPlayerDialog()
            }
        }
    }

    fun showEditPlayerDialog(jugador: Jugador) {
        _uiState.value = _uiState.value.copy(
            showEditPlayerDialog = true,
            selectedPlayer = jugador,
            newPlayerName = jugador.nombre,
            newPlayerColor = jugador.color
        )
    }

    fun hideEditPlayerDialog() {
        _uiState.value = _uiState.value.copy(
            showEditPlayerDialog = false,
            selectedPlayer = null,
            newPlayerName = "",
            newPlayerColor = "#E53935"
        )
    }

    fun updatePlayer() {
        val player = _uiState.value.selectedPlayer ?: return
        val nombre = _uiState.value.newPlayerName.trim()
        val color = _uiState.value.newPlayerColor

        if (nombre.isNotEmpty()) {
            viewModelScope.launch {
                repository.updateJugador(
                    player.copy(nombre = nombre, color = color)
                )
                hideEditPlayerDialog()
            }
        }
    }

    fun deletePlayer(jugador: Jugador) {
        viewModelScope.launch {
            repository.deleteJugador(jugador)
        }
    }

    fun deletePartida(partidaId: Long) {
        viewModelScope.launch {
            repository.deletePartida(partidaId)
            loadRanking()
        }
    }
}