package com.partxis.clasificacion.ui.screens.partida

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partxis.clasificacion.data.repository.PartxisRepository
import com.partxis.clasificacion.domain.model.Jugador
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NuevaPartidaUiState(
    val jugadores: List<Jugador> = emptyList(),
    val posiciones: Map<Long, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NuevaPartidaViewModel @Inject constructor(
    private val repository: PartxisRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val clasificacionId: Long = savedStateHandle.get<Long>("clasificacionId") ?: 0L

    private val _uiState = MutableStateFlow(NuevaPartidaUiState())
    val uiState: StateFlow<NuevaPartidaUiState> = _uiState.asStateFlow()

    init {
        loadJugadores()
    }

    private fun loadJugadores() {
        viewModelScope.launch {
            val jugadores = repository.getJugadoresByClasificacionSync(clasificacionId)
            val posiciones = jugadores.associate { it.id to 0 }
            _uiState.value = _uiState.value.copy(
                jugadores = jugadores,
                posiciones = posiciones,
                isLoading = false
            )
        }
    }

    fun updatePosicion(jugadorId: Long, posicion: Int) {
        val nuevasPosiciones = _uiState.value.posiciones.toMutableMap()
        nuevasPosiciones[jugadorId] = posicion
        _uiState.value = _uiState.value.copy(posiciones = nuevasPosiciones)
    }

    fun savePartida() {
        val posiciones = _uiState.value.posiciones

        if (posiciones.values.any { it == 0 }) {
            _uiState.value = _uiState.value.copy(error = "Todos los jugadores deben tener posición")
            return
        }

        if (posiciones.values.toSet().size != posiciones.size) {
            _uiState.value = _uiState.value.copy(error = "Las posiciones deben ser únicas")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true, error = null)

        viewModelScope.launch {
            try {
                val resultados = posiciones.map { (jugadorId, posicion) ->
                    jugadorId to posicion
                }
                repository.createPartida(clasificacionId, resultados)
                _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error al guardar la partida"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}