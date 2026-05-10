package com.partxis.clasificacion.data.local.dao

import androidx.room.*
import com.partxis.clasificacion.data.local.entity.JugadorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JugadorDao {
    @Query("SELECT * FROM jugadores WHERE clasificacionId = :clasificacionId")
    fun getJugadoresByClasificacion(clasificacionId: Long): Flow<List<JugadorEntity>>

    @Query("SELECT * FROM jugadores WHERE clasificacionId = :clasificacionId")
    suspend fun getJugadoresByClasificacionSync(clasificacionId: Long): List<JugadorEntity>

    @Query("SELECT * FROM jugadores WHERE id = :id")
    suspend fun getJugadorById(id: Long): JugadorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJugador(jugador: JugadorEntity): Long

    @Update
    suspend fun updateJugador(jugador: JugadorEntity)

    @Delete
    suspend fun deleteJugador(jugador: JugadorEntity)
}