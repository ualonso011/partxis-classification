package com.partxis.clasificacion.data.local.dao

import androidx.room.*
import com.partxis.clasificacion.data.local.entity.ClasificacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClasificacionDao {
    @Query("SELECT * FROM clasificaciones ORDER BY fechaCreacion DESC")
    fun getAllClasificaciones(): Flow<List<ClasificacionEntity>>

    @Query("SELECT * FROM clasificaciones WHERE id = :id")
    suspend fun getClasificacionById(id: Long): ClasificacionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasificacion(clasificacion: ClasificacionEntity): Long

    @Update
    suspend fun updateClasificacion(clasificacion: ClasificacionEntity)

    @Delete
    suspend fun deleteClasificacion(clasificacion: ClasificacionEntity)
}