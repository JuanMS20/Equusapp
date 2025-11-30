package com.villalobos.caballoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal de CaballoApp.
 * Anotada con @HiltAndroidApp para habilitar la inyección de dependencias con Hilt.
 */
@HiltAndroidApp
class CaballoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Inicialización global de la app si es necesario
    }
}
