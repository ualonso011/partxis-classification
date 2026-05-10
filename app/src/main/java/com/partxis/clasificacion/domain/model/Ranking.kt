package com.partxis.clasificacion.domain.model

data class RankingEntry(
    val posicion: Int,
    val jugador: Jugador,
    val puntosTotales: Int,
    val victorias: Int
)