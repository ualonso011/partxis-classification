package com.partxis.clasificacion.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "resultados_partida",
    primaryKeys = ["partidaId", "jugadorId"],
    foreignKeys = [
        ForeignKey(
            entity = PartidaEntity::class,
            parentColumns = ["id"],
            childColumns = ["partidaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = JugadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["jugadorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("partidaId"), Index("jugadorId")]
)
data class ResultadoPartidaEntity(
    val partidaId: Long,
    val jugadorId: Long,
    val posicion: Int,
    val puntos: Int = 0
)