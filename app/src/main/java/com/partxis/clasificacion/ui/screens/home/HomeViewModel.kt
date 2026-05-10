package com.partxis.clasificacion.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partxis.clasificacion.data.repository.PartxisRepository
import com.partxis.clasificacion.domain.model.Clasificacion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val clasificaciones: List<Clasificacion> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val newClasificacionName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PartxisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadClasificaciones()
    }

    private fun loadClasificaciones() {
        viewModelScope.launch {
            repository.getAllClasificaciones().collect { clasificaciones ->
                _uiState.value = _uiState.value.copy(
                    clasificaciones = clasificaciones,
                    isLoading = false
                )
            }
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = false,
            newClasificacionName = ""
        )
    }

    fun updateNewClasificacionName(name: String) {
        _uiState.value = _uiState.value.copy(newClasificacionName = name)
    }

    fun createClasificacion() {
        val name = _uiState.value.newClasificacionName.trim()
        if (name.isNotEmpty()) {
            viewModelScope.launch {
                repository.createClasificacion(name)
                hideCreateDialog()
            }
        }
    }

    fun deleteClasificacion(clasificacion: Clasificacion) {
        viewModelScope.launch {
            repository.deleteClasificacion(clasificacion.id)
        }
    }
}