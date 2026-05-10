package com.partxis.clasificacion.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "puntuaciones_posicion",
    foreignKeys = [
        ForeignKey(
            entity = ClasificacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["clasificacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clasificacionId")]
)
data class PuntuacionPosicionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clasificacionId: Long,
    val posicion: Int,
    val puntos: Int
)