package com.villalobos.caballoapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.data.model.AccessibilityConfig
import com.villalobos.caballoapp.data.model.ColorblindType
import com.villalobos.caballoapp.data.model.TextScale
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

/**
 * Objeto de utilidad para gestionar la accesibilidad en la aplicación.
 * Proporciona métodos para configurar descripciones de contenido, colores para daltónicos
 * y navegación por teclado.
 */
object AccesibilityHelper {

    private const val PREFS_NAME = "accessibility_prefs"
    private const val KEY_COLORBLIND_TYPE = "colorblind_type"
    private const val KEY_TEXT_SCALE = "text_scale"
    private const val KEY_HIGH_CONTRAST = "high_contrast"

    /**
     * Configura la descripción de contenido para una vista, mejorando la accesibilidad.
     *
     * @param view La vista a la que se le asignará la descripción.
     * @param description El texto que describirá la vista para los servicios de accesibilidad.
     * @param role El rol de la vista (ej. "Botón", "Encabezado").
     */
    fun setContentDescription(view: View, description: String, role: String) {
        view.contentDescription = "$role: $description"
    }

    /**
     * Aplica un filtro de color específico para daltónicos a una vista y sus hijos.
     *
     * @param context Contexto para acceder a los recursos.
     * @param rootView La vista raíz a la que se aplicará el filtro.
     * @param type El tipo de daltonismo para el que se aplicará el filtro.
     */
    fun applySpecificColorblindColors(context: Context, rootView: View, type: ColorblindType) {
        when (type) {
            ColorblindType.PROTANOPIA -> {
                // Lógica para Protanopia
            }
            ColorblindType.DEUTERANOPIA -> {
                // Lógica para Deuteranopia
            }
            ColorblindType.TRITANOPIA -> {
                // Lógica para Tritanopia
            }
            ColorblindType.ACHROMATOPSIA -> {
                // Lógica para Acromatopsia (escala de grises)
            }
            ColorblindType.NORMAL, ColorblindType.NONE -> {
                // Sin filtro
            }
        }
    }

    /**
     * Aplica un gradiente de fondo según el tipo de daltonismo.
     */
    fun applyBackgroundGradient(context: Context, rootView: View, type: ColorblindType) {
        val colors = when (type) {
            ColorblindType.PROTANOPIA -> intArrayOf(
                ContextCompat.getColor(context, R.color.primary_brown),
                ContextCompat.getColor(context, R.color.secondary_brown)
            )
            ColorblindType.DEUTERANOPIA -> intArrayOf(
                ContextCompat.getColor(context, R.color.primary_brown),
                ContextCompat.getColor(context, R.color.secondary_brown)
            )
            ColorblindType.TRITANOPIA -> intArrayOf(
                ContextCompat.getColor(context, R.color.primary_brown),
                ContextCompat.getColor(context, R.color.secondary_brown)
            )
            ColorblindType.ACHROMATOPSIA -> intArrayOf(
                Color.GRAY,
                Color.DKGRAY
            )
            ColorblindType.NORMAL, ColorblindType.NONE -> intArrayOf(
                ContextCompat.getColor(context, R.color.primary_brown),
                ContextCompat.getColor(context, R.color.secondary_brown)
            )
        }
        
        val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        rootView.background = gradient
    }

    /**
     * Obtiene la configuración de accesibilidad guardada en las preferencias.
     *
     * @param context Contexto para acceder a SharedPreferences.
     * @return La configuración de accesibilidad actual.
     */
    fun getAccessibilityConfig(context: Context): AccessibilityConfig {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colorblindTypeStr = prefs.getString(KEY_COLORBLIND_TYPE, "NORMAL") ?: "NORMAL"
        val textScaleStr = prefs.getString(KEY_TEXT_SCALE, "NORMAL") ?: "NORMAL"
        val highContrast = prefs.getBoolean(KEY_HIGH_CONTRAST, false)
        
        val colorblindType = ColorblindType.fromString(colorblindTypeStr)
        val textScale = try {
            TextScale.valueOf(textScaleStr)
        } catch (e: Exception) {
            TextScale.NORMAL
        }
        
        return AccessibilityConfig(
            colorblindType = colorblindType,
            textScale = textScale,
            highContrast = highContrast
        )
    }

    /**
     * Guarda la configuración de accesibilidad en las preferencias.
     */
    fun saveAccessibilityConfig(context: Context, config: AccessibilityConfig) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_COLORBLIND_TYPE, config.colorblindType.name)
            putString(KEY_TEXT_SCALE, config.textScale.name)
            putBoolean(KEY_HIGH_CONTRAST, config.highContrast)
            apply()
        }
    }

    /**
     * Aplica los colores de accesibilidad a toda la app.
     */
    fun applyAccessibilityColorsToApp(activity: Activity) {
        val config = getAccessibilityConfig(activity)
        applySpecificColorblindColors(activity, activity.window.decorView, config.colorblindType)
    }

    /**
     * Restaura los colores originales.
     */
    fun restoreOriginalColors(activity: Activity) {
        applySpecificColorblindColors(activity, activity.window.decorView, ColorblindType.NORMAL)
    }

    /**
     * Reinicia la app para aplicar cambios de colores.
     */
    fun restartAppForColorChanges(activity: Activity) {
        val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    /**
     * Configura la accesibilidad del dispositivo, como el tamaño de la fuente.
     *
     * @param context Contexto de la aplicación.
     * @param view La vista a la que se aplicarán los ajustes.
     */
    fun setupDeviceAccessibility(context: Context, view: View) {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (accessibilityManager.isEnabled) {
            // Aplicar configuraciones de accesibilidad del dispositivo
        }
    }

    /**
     * Asigna una descripción de contenido a la imagen anatómica.
     *
     * @param view La vista de la imagen anatómica interactiva.
     * @param regionName El nombre de la región que se muestra.
     * @param muscleCount El número de músculos en la región.
     */
    fun setAnatomicalImageDescription(view: InteractiveAnatomyView, regionName: String, muscleCount: Int) {
        view.contentDescription = "Imagen anatómica de la región de $regionName, mostrando $muscleCount músculos interactivos."
    }

    /**
     * Habilita la navegación por teclado entre la imagen y la lista de músculos.
     *
     * @param anatomyView La vista de la imagen anatómica.
     * @param recyclerView La lista de músculos.
     */
    fun enableKeyboardNavigation(anatomyView: View, recyclerView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            anatomyView.focusable = View.FOCUSABLE
            recyclerView.focusable = View.FOCUSABLE
            anatomyView.nextFocusForwardId = recyclerView.id
            recyclerView.nextFocusForwardId = anatomyView.id
        }
    }
}
