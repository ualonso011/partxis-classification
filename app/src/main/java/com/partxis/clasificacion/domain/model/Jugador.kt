package com.partxis.clasificacion.domain.model

import androidx.compose.ui.graphics.Color

data class Jugador(
    val id: Long = 0,
    val nombre: String,
    val color: String,
    val clasificacionId: Long,
    val puntosTotales: Int = 0,
    val victorias: Int = 0
) {
    fun getColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(color))
        } catch (e: Exception) {
            Color.Gray
        }
    }
}

object ColoresJugador {
    val colores = listOf(
        "Rojo" to "#E53935",
        "Azul" to "#1E88E5",
        "Verde" to "#43A047",
        "Amarillo" to "#FDD835",
        "Naranja" to "#FB8C00",
        "Violeta" to "#8E24AA",
        "Rosa" to "#D81B60",
        "Cyan" to "#00ACC1"
    )
}