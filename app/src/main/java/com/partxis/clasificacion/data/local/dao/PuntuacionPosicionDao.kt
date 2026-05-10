package com.partxis.clasificacion.data.local.dao

import androidx.room.*
import com.partxis.clasificacion.data.local.entity.PuntuacionPosicionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PuntuacionPosicionDao {
    @Query("SELECT * FROM puntuaciones_posicion WHERE clasificacionId = :clasificacionId ORDER BY posicion")
    fun getPuntuacionesByClasificacion(clasificacionId: Long): Flow<List<PuntuacionPosicionEntity>>

    @Query("SELECT * FROM puntuaciones_posicion WHERE clasificacionId = :clasificacionId ORDER BY posicion")
    suspend fun getPuntuacionesByClasificacionSync(clasificacionId: Long): List<PuntuacionPosicionEntity>

    @Query("SELECT * FROM puntuaciones_posicion WHERE clasificacionId = :clasificacionId AND posicion = :posicion")
    suspend fun getPuntuacionByPosicion(clasificacionId: Long, posicion: Int): PuntuacionPosicionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuntuaciones(puntuaciones: List<PuntuacionPosicionEntity>)

    @Query("DELETE FROM puntuaciones_posicion WHERE clasificacionId = :clasificacionId")
    suspend fun deletePuntuacionesByClasificacion(clasificacionId: Long)
}