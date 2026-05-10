package com.partxis.clasificacion.data.local.dao

import androidx.room.*
import com.partxis.clasificacion.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {
    @Query("SELECT * FROM partidas WHERE clasificacionId = :clasificacionId ORDER BY fecha DESC")
    fun getPartidasByClasificacion(clasificacionId: Long): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE id = :id")
    suspend fun getPartidaById(id: Long): PartidaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartida(partida: PartidaEntity): Long

    @Delete
    suspend fun deletePartida(partida: PartidaEntity)
}