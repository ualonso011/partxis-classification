package com.partxis.clasificacion.domain.model

data class Clasificacion(
    val id: Long = 0,
    val nombre: String,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val puntuacionInvertida: Boolean = false
)