package com.partxis.clasificacion.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partxis.clasificacion.data.local.entity.PuntuacionPosicionEntity
import com.partxis.clasificacion.data.repository.PartxisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditPuntuacionesUiState(
    val puntuaciones: List<PuntuacionPosicionEntity> = emptyList(),
    val maxPosiciones: Int = 8,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val puntuacionInvertida: Boolean = false
)

@HiltViewModel
class EditPuntuacionesViewModel @Inject constructor(
    private val repository: PartxisRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val clasificacionId: Long = savedStateHandle.get<Long>("clasificacionId") ?: 0L

    private val _uiState = MutableStateFlow(EditPuntuacionesUiState())
    val uiState: StateFlow<EditPuntuacionesUiState> = _uiState.asStateFlow()

    init {
        loadPuntuaciones()
    }

    private fun loadPuntuaciones() {
        viewModelScope.launch {
            repository.getPuntuacionesByClasificacion(clasificacionId).collect { puntuaciones ->
                val clasificacion = repository.getClasificacionById(clasificacionId)
                val defaultPuntuaciones = (1.._uiState.value.maxPosiciones).map { posicion ->
                    puntuaciones.find { it.posicion == posicion }
                        ?: PuntuacionPosicionEntity(
                            clasificacionId = clasificacionId,
                            posicion = posicion,
                            puntos = getDefaultPuntos(posicion)
                        )
                }
                _uiState.value = _uiState.value.copy(
                    puntuaciones = defaultPuntuaciones,
                    puntuacionInvertida = clasificacion?.puntuacionInvertida ?: false,
                    isLoading = false
                )
            }
        }
    }

    private fun getDefaultPuntos(posicion: Int): Int {
        return when (posicion) {
            1 -> 3
            2 -> 2
            3 -> 1
            else -> 0
        }
    }

    fun updatePuntuacion(posicion: Int, puntos: Int) {
        val nuevasPuntuaciones = _uiState.value.puntuaciones.toMutableList()
        val index = nuevasPuntuaciones.indexOfFirst { it.posicion == posicion }
        if (index >= 0) {
            nuevasPuntuaciones[index] = nuevasPuntuaciones[index].copy(puntos = puntos)
            _uiState.value = _uiState.value.copy(puntuaciones = nuevasPuntuaciones)
        }
    }

    fun setPuntuacionInvertida(invertido: Boolean) {
        _uiState.value = _uiState.value.copy(puntuacionInvertida = invertido)
    }

    fun savePuntuaciones() {
        _uiState.value = _uiState.value.copy(isSaving = true)

        viewModelScope.launch {
            val puntuaciones = _uiState.value.puntuaciones
                .filter { it.puntos > 0 }
                .map { it.posicion to it.puntos }
            repository.savePuntuaciones(clasificacionId, puntuaciones)

            val clasificacion = repository.getClasificacionById(clasificacionId)
            clasificacion?.let {
                repository.updateClasificacion(
                    it.copy(puntuacionInvertida = _uiState.value.puntuacionInvertida)
                )
            }

            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}