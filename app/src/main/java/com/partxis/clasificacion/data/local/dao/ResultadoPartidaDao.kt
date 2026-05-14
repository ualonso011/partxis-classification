package com.partxis.clasificacion.data.local.dao

import androidx.room.*
import com.partxis.clasificacion.data.local.entity.ResultadoPartidaEntity

@Dao
interface ResultadoPartidaDao {
    @Query("SELECT * FROM resultados_partida WHERE partidaId = :partidaId")
    suspend fun getResultadosByPartida(partidaId: Long): List<ResultadoPartidaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResultados(resultados: List<ResultadoPartidaEntity>)

    @Query("DELETE FROM resultados_partida WHERE partidaId = :partidaId")
    suspend fun deleteResultadosByPartida(partidaId: Long)

    @Query("""
        SELECT COALESCE(SUM(rp.puntos), 0)
        FROM resultados_partida rp
        WHERE rp.jugadorId = :jugadorId
    """)
    suspend fun getTotalPuntosByJugador(jugadorId: Long): Int

    @Query("""
        SELECT rp.jugadorId, SUM(rp.puntos) as totalPuntos
        FROM resultados_partida rp
        INNER JOIN partidas p ON p.id = rp.partidaId
        WHERE p.clasificacionId = :clasificacionId
        GROUP BY rp.jugadorId
    """)
    suspend fun getRankingByClasificacion(clasificacionId: Long): List<JugadorPuntos>
}

data class JugadorPuntos(
    val jugadorId: Long,
    val totalPuntos: Int
)