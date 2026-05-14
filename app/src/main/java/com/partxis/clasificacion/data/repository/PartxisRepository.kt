package com.partxis.clasificacion.data.repository

import com.partxis.clasificacion.data.local.dao.*
import com.partxis.clasificacion.data.local.entity.*
import com.partxis.clasificacion.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PartxisRepository @Inject constructor(
    private val clasificacionDao: ClasificacionDao,
    private val jugadorDao: JugadorDao,
    private val partidaDao: PartidaDao,
    private val resultadoPartidaDao: ResultadoPartidaDao,
    private val puntuacionPosicionDao: PuntuacionPosicionDao
) {
    private val _partidaSaved = MutableSharedFlow<Long>()
    val partidaSaved: Flow<Long> = _partidaSaved.asSharedFlow()

    fun getAllClasificaciones(): Flow<List<Clasificacion>> {
        return clasificacionDao.getAllClasificaciones().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getClasificacionById(id: Long): Clasificacion? {
        return clasificacionDao.getClasificacionById(id)?.toDomain()
    }

    suspend fun createClasificacion(nombre: String): Long {
        return clasificacionDao.insertClasificacion(ClasificacionEntity(nombre = nombre))
    }

    suspend fun updateClasificacion(clasificacion: Clasificacion) {
        clasificacionDao.updateClasificacion(clasificacion.toEntity())
    }

    suspend fun deleteClasificacion(id: Long) {
        val entity = clasificacionDao.getClasificacionById(id)
        entity?.let { clasificacionDao.deleteClasificacion(it) }
    }

    fun getJugadoresByClasificacion(clasificacionId: Long): Flow<List<Jugador>> {
        return jugadorDao.getJugadoresByClasificacion(clasificacionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getJugadoresByClasificacionSync(clasificacionId: Long): List<Jugador> {
        return jugadorDao.getJugadoresByClasificacionSync(clasificacionId).map { it.toDomain() }
    }

    suspend fun addJugador(nombre: String, color: String, clasificacionId: Long): Long {
        return jugadorDao.insertJugador(
            JugadorEntity(nombre = nombre, color = color, clasificacionId = clasificacionId)
        )
    }

    suspend fun updateJugador(jugador: Jugador) {
        jugadorDao.updateJugador(jugador.toEntity())
    }

    suspend fun deleteJugador(jugador: Jugador) {
        jugadorDao.deleteJugador(jugador.toEntity())
    }

    fun getPartidasByClasificacion(clasificacionId: Long): Flow<List<Partida>> {
        return partidaDao.getPartidasByClasificacion(clasificacionId).map { entities ->
            entities.map { entity ->
                val resultados = resultadoPartidaDao.getResultadosByPartida(entity.id)
                val resultadosWithJugadores = resultados.mapNotNull { resultado ->
                    val jugador = jugadorDao.getJugadorById(resultado.jugadorId)
                    jugador?.let {
                        Resultado(it.toDomain(), resultado.posicion, resultado.puntos)
                    }
                }
                entity.toDomain(resultadosWithJugadores)
            }
        }
    }

    suspend fun createPartida(clasificacionId: Long, resultados: List<Pair<Long, Int>>): Long {
        val partidaId = partidaDao.insertPartida(
            PartidaEntity(clasificacionId = clasificacionId)
        )

        val puntuaciones = puntuacionPosicionDao.getPuntuacionesByClasificacionSync(clasificacionId)
        val puntosMap = puntuaciones.associateBy { it.posicion }

        val resultadosEntities = resultados.map { (jugadorId, posicion) ->
            val puntos = puntosMap[posicion]?.puntos ?: 0
            ResultadoPartidaEntity(
                partidaId = partidaId,
                jugadorId = jugadorId,
                posicion = posicion,
                puntos = puntos
            )
        }

        resultadoPartidaDao.insertResultados(resultadosEntities)
        _partidaSaved.emit(partidaId)
        return partidaId
    }

    suspend fun deletePartida(partidaId: Long) {
        val partida = partidaDao.getPartidaById(partidaId)
        partida?.let { partidaDao.deletePartida(it) }
    }

    suspend fun getRanking(clasificacionId: Long, invertido: Boolean = false): List<RankingEntry> {
        val ranking = resultadoPartidaDao.getRankingByClasificacion(clasificacionId)
        val sorted = if (invertido) {
            ranking.sortedBy { it.totalPuntos }
        } else {
            ranking.sortedByDescending { it.totalPuntos }
        }
        val jugadores = jugadorDao.getJugadoresByClasificacionSync(clasificacionId)
        val jugadorMap = jugadores.associateBy { it.id }

        val partidas = mutableMapOf<Long, Int>()
        sorted.forEach { rp ->
            val jugador = jugadorMap[rp.jugadorId]
            jugador?.let {
                val victorias = resultadoPartidaDao.getResultadosByPartida(rp.jugadorId)
                    .count { it.posicion == 1 }
                partidas[rp.jugadorId] = victorias
            }
        }

        return sorted.mapIndexed { index, rp ->
            val jugador = jugadorMap[rp.jugadorId]
            RankingEntry(
                posicion = index + 1,
                jugador = jugador?.toDomain() ?: Jugador(0, "", "", clasificacionId),
                puntosTotales = rp.totalPuntos,
                victorias = partidas[rp.jugadorId] ?: 0
            )
        }
    }

    fun getPuntuacionesByClasificacion(clasificacionId: Long): Flow<List<PuntuacionPosicionEntity>> {
        return puntuacionPosicionDao.getPuntuacionesByClasificacion(clasificacionId)
    }

    suspend fun savePuntuaciones(clasificacionId: Long, puntuaciones: List<Pair<Int, Int>>) {
        puntuacionPosicionDao.deletePuntuacionesByClasificacion(clasificacionId)
        val entities = puntuaciones.map { (posicion, puntos) ->
            PuntuacionPosicionEntity(
                clasificacionId = clasificacionId,
                posicion = posicion,
                puntos = puntos
            )
        }
        puntuacionPosicionDao.insertPuntuaciones(entities)
    }

    private fun ClasificacionEntity.toDomain() = Clasificacion(id, nombre, fechaCreacion, puntuacionInvertida)
    private fun Clasificacion.toEntity() = ClasificacionEntity(id, nombre, fechaCreacion, puntuacionInvertida)
    private fun JugadorEntity.toDomain() = Jugador(id, nombre, color, clasificacionId)
    private fun Jugador.toEntity() = JugadorEntity(id, nombre, color, clasificacionId)
    private fun PartidaEntity.toDomain(resultados: List<Resultado> = emptyList()) =
        Partida(id, clasificacionId, fecha, resultados)
}