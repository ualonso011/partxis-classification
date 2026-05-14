package com.partxis.clasificacion.di

import android.content.Context
import androidx.room.Room
import com.partxis.clasificacion.data.local.PartxisDatabase
import com.partxis.clasificacion.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PartxisDatabase {
        return Room.databaseBuilder(
            context,
            PartxisDatabase::class.java,
            "partxis_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideClasificacionDao(database: PartxisDatabase): ClasificacionDao {
        return database.clasificacionDao()
    }

    @Provides
    fun provideJugadorDao(database: PartxisDatabase): JugadorDao {
        return database.jugadorDao()
    }

    @Provides
    fun providePartidaDao(database: PartxisDatabase): PartidaDao {
        return database.partidaDao()
    }

    @Provides
    fun provideResultadoPartidaDao(database: PartxisDatabase): ResultadoPartidaDao {
        return database.resultadoPartidaDao()
    }

    @Provides
    fun providePuntuacionPosicionDao(database: PartxisDatabase): PuntuacionPosicionDao {
        return database.puntuacionPosicionDao()
    }
}