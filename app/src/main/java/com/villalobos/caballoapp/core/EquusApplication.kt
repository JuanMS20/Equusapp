package com.villalobos.caballoapp.core

import android.app.Application
import com.villalobos.caballoapp.data.repository.GamificationRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Clase Application principal de EquusApp.
 * Anotada con @HiltAndroidApp para habilitar la inyección de dependencias con Hilt.
 */
@HiltAndroidApp
class EquusApplication : Application() {

    @Inject
    lateinit var gamificationRepository: GamificationRepository
    
    override fun onCreate() {
        super.onCreate()
        // Inicialización global de la app si es necesario

        // Inicializar datos de gamificación si es necesario
        CoroutineScope(Dispatchers.IO).launch {
            gamificationRepository.initializeDataIfNeeded()
        }
    }
}
