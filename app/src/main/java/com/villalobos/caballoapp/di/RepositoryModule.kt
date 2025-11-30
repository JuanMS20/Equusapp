package com.villalobos.caballoapp.di

import android.content.Context
import android.content.SharedPreferences
import com.villalobos.caballoapp.data.repository.AccessibilityRepository
import com.villalobos.caballoapp.data.repository.MusculoRepository
import com.villalobos.caballoapp.data.repository.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer Repositories.
 * Centraliza la creación de dependencias de datos.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // ============ SharedPreferences ============

    @Provides
    @Singleton
    @Named("quiz_prefs")
    fun provideQuizPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("quiz_stats", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("accessibility_prefs")
    fun provideAccessibilityPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("accessibility_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @Named("tutorial_prefs")
    fun provideTutorialPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
    }

    // ============ Repositories ============

    @Provides
    @Singleton
    fun provideQuizRepository(@ApplicationContext context: Context): QuizRepository {
        return QuizRepository(context)
    }

    @Provides
    @Singleton
    fun provideAccessibilityRepository(@ApplicationContext context: Context): AccessibilityRepository {
        return AccessibilityRepository(context)
    }

    @Provides
    @Singleton
    fun provideMusculoRepository(): MusculoRepository {
        return MusculoRepository()
    }
}
