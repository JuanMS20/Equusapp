package com.villalobos.caballoapp.ui.base

import android.os.Bundle
import com.villalobos.caballoapp.util.AccesibilityHelper

/**
 * Clase base para actividades que aplican automáticamente la configuración de accesibilidad
 * Todas las actividades deberían extender esta clase o llamar a applyAccessibilityOnStart()
 */
abstract class AccessibilityActivity : BaseNavigationActivity() {
    
    private var hasAppliedAccessibility = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Solo aplicar fondo en onCreate, el resto se hace en onResume
    }
    
    override fun onResume() {
        super.onResume()
        
        // Aplicar accesibilidad solo una vez o si cambió la configuración
        if (!hasAppliedAccessibility) {
            applyAccessibilityLightweight()
            hasAppliedAccessibility = true
        }
    }
    
    /**
     * Aplica la configuración de accesibilidad de forma ligera (solo fondo)
     */
    private fun applyAccessibilityLightweight() {
        try {
            val config = AccesibilityHelper.getAccessibilityConfig(this)
            // Solo aplicar el fondo, no recorrer todo el árbol de vistas
            AccesibilityHelper.applyBackgroundGradient(this, window.decorView, config.colorblindType)
            // Permitir a subclases aplicar colores específicos
            applyActivityAccessibilityColors()
        } catch (e: Exception) {
            // Ignorar errores silenciosamente
        }
    }
    
    /**
     * Fuerza re-aplicación de accesibilidad (llamar cuando cambie la configuración)
     */
    fun forceReapplyAccessibility() {
        hasAppliedAccessibility = false
        applyAccessibilityLightweight()
        hasAppliedAccessibility = true
    }
}
