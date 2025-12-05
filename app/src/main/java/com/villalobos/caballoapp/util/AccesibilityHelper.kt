package com.villalobos.caballoapp.util

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.data.model.AccessibilityConfig
import com.villalobos.caballoapp.data.model.ColorblindType
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

/**
 * Objeto de utilidad para gestionar la accesibilidad en la aplicación.
 * Proporciona métodos para configurar descripciones de contenido, colores para daltónicos
 * y navegación por teclado.
 */
object AccesibilityHelper {

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
            ColorblindType.NORMAL -> {
                // Sin filtro
            }
        }
    }

    /**
     * Obtiene la configuración de accesibilidad guardada en las preferencias.
     *
     * @param context Contexto para acceder a SharedPreferences.
     * @return La configuración de accesibilidad actual.
     */
    fun getAccessibilityConfig(context: Context): AccessibilityConfig {
        // Lógica para obtener la configuración desde SharedPreferences
        return AccessibilityConfig(ColorblindType.NORMAL, 1.0f)
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
