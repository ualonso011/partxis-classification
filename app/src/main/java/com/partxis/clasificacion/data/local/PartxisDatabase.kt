package com.partxis.clasificacion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.partxis.clasificacion.data.local.dao.*
import com.partxis.clasificacion.data.local.entity.*

@Database(
    entities = [
        ClasificacionEntity::class,
        JugadorEntity::class,
        PartidaEntity::class,
        ResultadoPartidaEntity::class,
        PuntuacionPosicionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PartxisDatabase : RoomDatabase() {
    abstract fun clasificacionDao(): ClasificacionDao
    abstract fun jugadorDao(): JugadorDao
    abstract fun partidaDao(): PartidaDao
    abstract fun resultadoPartidaDao(): ResultadoPartidaDao
    abstract fun puntuacionPosicionDao(): PuntuacionPosicionDao
}