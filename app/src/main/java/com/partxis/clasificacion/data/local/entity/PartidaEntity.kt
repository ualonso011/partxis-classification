package com.partxis.clasificacion.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "partidas",
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
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clasificacionId: Long,
    val fecha: Long = System.currentTimeMillis()
)