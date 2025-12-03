package com.villalobos.caballoapp.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.villalobos.caballoapp.util.AccesibilityHelper
import com.villalobos.caballoapp.ui.main.MainActivity

/**
 * Repository para manejar configuración de accesibilidad.
 * Centraliza el acceso y persistencia de preferencias de accesibilidad.
 */
class AccessibilityRepository(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "accessibility_prefs"
        private const val KEY_COLORBLIND_TYPE = "colorblind_type"
        private const val KEY_HIGH_CONTRAST = "high_contrast"
        private const val KEY_LARGE_TEXT = "large_text"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ============ Configuración ============

    /**
     * Obtiene la configuración actual de accesibilidad.
     */
    fun getAccessibilityConfig(): AccesibilityHelper.AccessibilityConfig {
        return AccesibilityHelper.getAccessibilityConfig(context)
    }

    /**
     * Guarda la configuración de accesibilidad.
     */
    fun saveAccessibilityConfig(config: AccesibilityHelper.AccessibilityConfig) {
        AccesibilityHelper.saveAccessibilityConfig(context, config)
    }

    /**
     * Obtiene el tipo de daltonismo configurado.
     */
    fun getColorblindType(): AccesibilityHelper.ColorblindType {
        val config = getAccessibilityConfig()
        return config.colorblindType
    }

    /**
     * Establece el tipo de daltonismo.
     */
    fun setColorblindType(type: AccesibilityHelper.ColorblindType) {
        val currentConfig = getAccessibilityConfig()
        val newConfig = currentConfig.copy(colorblindType = type)
        saveAccessibilityConfig(newConfig)
    }

    /**
     * Verifica si el alto contraste está habilitado.
     */
    fun isHighContrastEnabled(): Boolean {
        val config = getAccessibilityConfig()
        return config.highContrast
    }

    /**
     * Establece el alto contraste.
     */
    fun setHighContrast(enabled: Boolean) {
        val currentConfig = getAccessibilityConfig()
        val newConfig = currentConfig.copy(highContrast = enabled)
        saveAccessibilityConfig(newConfig)
    }

    /**
     * Verifica si el texto grande está habilitado.
     */
    fun isLargeTextEnabled(): Boolean {
        val config = getAccessibilityConfig()
        return config.textScale == AccesibilityHelper.TextScale.LARGE || 
               config.textScale == AccesibilityHelper.TextScale.EXTRA_LARGE
    }

    /**
     * Establece el texto grande.
     */
    fun setLargeText(enabled: Boolean) {
        val currentConfig = getAccessibilityConfig()
        val newScale = if (enabled) AccesibilityHelper.TextScale.LARGE else AccesibilityHelper.TextScale.NORMAL
        val newConfig = currentConfig.copy(textScale = newScale)
        saveAccessibilityConfig(newConfig)
    }

    // ============ Aplicación de colores ============

    /**
     * Aplica los colores de accesibilidad a una actividad.
     */
    fun applyAccessibilityColors(activity: Activity) {
        AccesibilityHelper.applyAccessibilityColorsToApp(activity)
    }

    /**
     * Restaura los colores originales.
     */
    fun restoreOriginalColors(activity: Activity) {
        AccesibilityHelper.restoreOriginalColors(activity)
    }

    /**
     * Reinicia la app para aplicar cambios de colores.
     */
    fun restartAppForColorChanges(activity: Activity) {
        AccesibilityHelper.restartAppForColorChanges(activity)
    }

    // ============ Utilidades ============

    /**
     * Obtiene el nombre legible del tipo de daltonismo.
     */
    fun getColorblindTypeName(type: AccesibilityHelper.ColorblindType): String {
        return when (type) {
            AccesibilityHelper.ColorblindType.NONE -> "Colores estándar"
            AccesibilityHelper.ColorblindType.PROTANOPIA -> "Protanopia"
            AccesibilityHelper.ColorblindType.DEUTERANOPIA -> "Deuteranopia"
            AccesibilityHelper.ColorblindType.TRITANOPIA -> "Tritanopia"
            AccesibilityHelper.ColorblindType.ACHROMATOPSIA -> "Acromatopsia"
        }
    }

    /**
     * Obtiene la descripción del tipo de daltonismo.
     */
    fun getColorblindTypeDescription(type: AccesibilityHelper.ColorblindType): String {
        return when (type) {
            AccesibilityHelper.ColorblindType.NONE -> "Visión normal de colores"
            AccesibilityHelper.ColorblindType.PROTANOPIA -> "Dificultad para distinguir rojos"
            AccesibilityHelper.ColorblindType.DEUTERANOPIA -> "Dificultad para distinguir verdes"
            AccesibilityHelper.ColorblindType.TRITANOPIA -> "Dificultad para distinguir azules y amarillos"
            AccesibilityHelper.ColorblindType.ACHROMATOPSIA -> "Visión en escala de grises"
        }
    }
}
