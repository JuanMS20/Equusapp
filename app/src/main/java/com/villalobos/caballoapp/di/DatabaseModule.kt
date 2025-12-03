package com.villalobos.caballoapp.di

import android.content.Context
import androidx.room.Room
import com.villalobos.caballoapp.data.source.local.AppDatabase
import com.villalobos.caballoapp.data.source.local.GamificationDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "equusapp_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGamificationDao(database: AppDatabase): GamificationDao {
        return database.gamificationDao()
    }
}