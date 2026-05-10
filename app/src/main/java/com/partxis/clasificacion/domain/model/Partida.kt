package com.partxis.clasificacion.domain.model

data class Partida(
    val id: Long = 0,
    val clasificacionId: Long,
    val fecha: Long = System.currentTimeMillis(),
    val resultados: List<Resultado> = emptyList()
)

data class Resultado(
    val jugador: Jugador,
    val posicion: Int,
    val puntos: Int
)