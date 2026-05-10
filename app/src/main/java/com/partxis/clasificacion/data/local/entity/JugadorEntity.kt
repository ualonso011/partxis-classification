package com.partxis.clasificacion.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jugadores",
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
data class JugadorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val color: String,
    val clasificacionId: Long
)