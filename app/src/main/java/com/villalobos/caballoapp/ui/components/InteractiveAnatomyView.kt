package com.villalobos.caballoapp.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.villalobos.caballoapp.BuildConfig
import com.villalobos.caballoapp.data.model.Musculo
import com.villalobos.caballoapp.R
import kotlin.math.hypot

/**
 * Vista personalizada para mostrar imágenes anatómicas interactivas.
 * 
 * Detecta toques sobre músculos usando coordenadas normalizadas (0.0 a 1.0),
 * lo que garantiza funcionamiento correcto independientemente del tamaño de pantalla,
 * zoom o escalado de la imagen.
 * 
 * Ventajas sobre HotspotHelper:
 * - No crea múltiples Views invisibles (menor consumo de memoria)
 * - Funciona correctamente con cualquier scaleType
 * - Cálculo matemático preciso de detección táctil
 * - Soporte para feedback visual opcional
 * 
 * Uso:
 * ```kotlin
 * interactiveAnatomyView.setMusculos(musculos) { musculo ->
 *     // Manejar clic en músculo
 * }
 * ```
 */
class InteractiveAnatomyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "InteractiveAnatomyView"
        
        /**
         * Radio de tolerancia por defecto (15% para debug, usar 0.06f en producción).
         */
        const val DEFAULT_TOUCH_TOLERANCE = 0.06f

        /**
         * Duración del feedback visual en milisegundos.
         */
        const val FEEDBACK_DURATION_MS = 500L

        /**
         * Transparencia del círculo de feedback (0-255).
         */
        const val FEEDBACK_ALPHA = 100

        /**
         * Factor para calcular el radio del círculo de feedback.
         */
        const val FEEDBACK_RADIUS_FACTOR = 0.04f
        
        /**
         * Radio de los círculos de debug en píxeles.
         */
        const val DEBUG_CIRCLE_RADIUS = 24f
    }

    // ============ Configuración ============

    /**
     * Radio de tolerancia para detectar toques cercanos a un hotspot.
     * Valor en coordenadas normalizadas (0.0 a 1.0).
     */
    var touchTolerance: Float = DEFAULT_TOUCH_TOLERANCE
        set(value) {
            field = value.coerceIn(0.02f, 0.30f)
        }

    /**
     * Habilita/deshabilita el feedback visual al detectar un músculo.
     */
    var showTouchFeedback: Boolean = true

    /**
     * Duración del feedback visual en milisegundos.
     */
    var feedbackDuration: Long = FEEDBACK_DURATION_MS
    
    /**
     * DEBUG: Habilita la visualización de hotspots y puntos de toque.
     * ¡Desactivar en producción estableciendo a false!
     */
    var debugMode: Boolean = false

    // ============ Estado interno ============

    private var musculos: List<Musculo> = emptyList()
    private var onMusculoClickListener: ((Musculo) -> Unit)? = null

    // Para feedback visual
    private var lastTouchedPoint: PointF? = null
    private var lastTouchedMusculo: Musculo? = null
    
    // DEBUG: Último punto tocado en pantalla
    private var lastTouchScreenPoint: PointF? = null
    
    // Handler para cancelar postDelayed pendientes
    private val feedbackHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var pendingFeedbackRunnable: Runnable? = null
    
    private val feedbackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        alpha = FEEDBACK_ALPHA
    }
    private val feedbackStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        alpha = 200
    }
    
    // DEBUG: Paint para hotspots de músculos (ROJO)
    private val debugHotspotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
        alpha = 180
    }
    private val debugHotspotStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    // DEBUG: Paint para punto de toque (AZUL)
    private val debugTouchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        alpha = 200
    }
    private val debugTouchStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    // DEBUG: Paint para texto
    private val debugTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
    }
    
    // DEBUG: Paint para área de tolerancia (VERDE)
    private val debugTolerancePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 2f
        alpha = 150
    }

    // Matriz inversa para transformar coordenadas de pantalla a imagen
    private val inverseMatrix = Matrix()

    init {
        // Configurar colores de feedback
        feedbackPaint.color = ContextCompat.getColor(context, R.color.primary_brown)
        feedbackStrokePaint.color = ContextCompat.getColor(context, R.color.accent_gold)

        // Habilitar clics
        isClickable = true
        isFocusable = true
        
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "✅ InteractiveAnatomyView inicializada. debugMode=$debugMode, touchTolerance=$touchTolerance")
        }
    }

    // ============ API Pública ============

    /**
     * Configura la lista de músculos detectables y el listener de clics.
     * 
     * @param musculos Lista de músculos con coordenadas hotspotX/hotspotY (0.0 a 1.0)
     * @param listener Callback invocado cuando se toca un músculo válido
     */
    fun setMusculos(musculos: List<Musculo>, listener: (Musculo) -> Unit) {
        this.musculos = musculos
        this.onMusculoClickListener = listener
        
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "📋 setMusculos() llamado con ${musculos.size} músculos:")
            musculos.forEachIndexed { index, m ->
                Log.d(TAG, "   [$index] ${m.nombre}: hotspot=(${m.hotspotX}, ${m.hotspotY})")
            }
        }
        
        // Forzar redibujado para mostrar hotspots de debug
        invalidate()
        
        // Actualizar content description para accesibilidad
        contentDescription = context.getString(
            R.string.anatomia_interactiva_description,
            musculos.size
        )
    }

    /**
     * Limpia los músculos y el listener.
     */
    fun clearMusculos() {
        musculos = emptyList()
        onMusculoClickListener = null
        lastTouchedPoint = null
        lastTouchedMusculo = null
        lastTouchScreenPoint = null
        
        // Cancelar cualquier runnable pendiente
        cancelPendingFeedback()
        
        invalidate()
    }
    
    /**
     * Cancela cualquier feedback visual pendiente.
     * Llamar en onDestroy() de la Activity o cuando se limpie la vista.
     */
    fun cancelPendingFeedback() {
        pendingFeedbackRunnable?.let { 
            feedbackHandler.removeCallbacks(it)
        }
        pendingFeedbackRunnable = null
    }

    /**
     * Obtiene el músculo en las coordenadas normalizadas especificadas.
     * 
     * @param normalizedX Coordenada X normalizada (0.0 a 1.0)
     * @param normalizedY Coordenada Y normalizada (0.0 a 1.0)
     * @return El músculo más cercano dentro del radio de tolerancia, o null
     */
    fun getMusculoAt(normalizedX: Float, normalizedY: Float): Musculo? {
        return findNearestMusculo(normalizedX, normalizedY)
    }

    // ============ Detección Táctil ============

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (musculos.isEmpty()) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "⚠️ onTouchEvent: Lista de músculos VACÍA - no se procesará el toque")
            }
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val screenX = event.x
                val screenY = event.y
                
                // DEBUG: Guardar punto de toque en pantalla
                lastTouchScreenPoint = PointF(screenX, screenY)
                
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "👆 ACTION_DOWN en pantalla: ($screenX, $screenY)")
                    Log.d(TAG, "   View size: ${width}x${height}")
                }
                
                // Convertir coordenadas de pantalla a normalizadas
                val normalized = screenToNormalizedCoordinates(screenX, screenY)
                
                if (normalized != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "   Coordenadas normalizadas: (${normalized.x}, ${normalized.y})")
                        
                        // DEBUG: Calcular y mostrar distancia a TODOS los músculos
                        musculos.forEach { m ->
                            val dist = hypot(
                                (normalized.x - m.hotspotX).toDouble(),
                                (normalized.y - m.hotspotY).toDouble()
                            ).toFloat()
                            val dentro = if (dist < touchTolerance) "✅ DENTRO" else "❌ fuera"
                            Log.d(TAG, "   -> ${m.nombre}: dist=${"%.4f".format(dist)} (tolerancia=$touchTolerance) $dentro")
                        }
                    }
                    
                    val musculo = findNearestMusculo(normalized.x, normalized.y)
                    
                    if (musculo != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "   🎯 Músculo DETECTADO: ${musculo.nombre}")
                        }
                        lastTouchedPoint = normalized
                        lastTouchedMusculo = musculo
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "   ❌ Ningún músculo dentro del rango de tolerancia")
                        }
                        lastTouchedPoint = normalized
                        lastTouchedMusculo = null
                    }
                    
                    invalidate()
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "   ⚠️ Toque FUERA de los límites de la imagen")
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                val normalized = screenToNormalizedCoordinates(event.x, event.y)
                
                if (normalized != null) {
                    val musculo = findNearestMusculo(normalized.x, normalized.y)
                    
                    if (musculo != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "👆 ACTION_UP - Invocando listener para: ${musculo.nombre}")
                        }
                        // Invocar listener
                        onMusculoClickListener?.invoke(musculo)
                        
                        // Limpiar feedback después de un delay
                        if (showTouchFeedback) {
                            // Cancelar cualquier runnable anterior
                            cancelPendingFeedback()
                            
                            pendingFeedbackRunnable = Runnable {
                                lastTouchedPoint = null
                                lastTouchedMusculo = null
                                lastTouchScreenPoint = null
                                invalidate()
                            }
                            feedbackHandler.postDelayed(pendingFeedbackRunnable!!, feedbackDuration)
                        }
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "👆 ACTION_UP - Sin músculo detectado")
                        }
                        // Limpiar después de un delay para ver el debug
                        cancelPendingFeedback()
                        
                        pendingFeedbackRunnable = Runnable {
                            lastTouchedPoint = null
                            lastTouchedMusculo = null
                            lastTouchScreenPoint = null
                            invalidate()
                        }
                        feedbackHandler.postDelayed(pendingFeedbackRunnable!!, 2000L) // 2 segundos para debug
                    }
                }
                
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                lastTouchedPoint = null
                lastTouchedMusculo = null
                lastTouchScreenPoint = null
                invalidate()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    // ============ Conversión de Coordenadas ============

    /**
     * Convierte coordenadas de pantalla (píxeles) a coordenadas normalizadas (0.0-1.0).
     * Tiene en cuenta el scaleType y la matriz de transformación de la imagen.
     * 
     * @param screenX Coordenada X en píxeles de pantalla
     * @param screenY Coordenada Y en píxeles de pantalla
     * @return Punto con coordenadas normalizadas, o null si está fuera de la imagen
     */
    private fun screenToNormalizedCoordinates(screenX: Float, screenY: Float): PointF? {
        val drawable = drawable ?: run {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "❌ screenToNormalized: drawable es NULL")
            }
            return null
        }
        
        val intrinsicWidth = drawable.intrinsicWidth.toFloat()
        val intrinsicHeight = drawable.intrinsicHeight.toFloat()
        
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "❌ screenToNormalized: dimensiones inválidas ($intrinsicWidth x $intrinsicHeight)")
            }
            return null
        }
        
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "   Drawable intrinsic size: $intrinsicWidth x $intrinsicHeight")
        }

        // Obtener la matriz inversa de transformación
        imageMatrix.invert(inverseMatrix)
        
        // Transformar las coordenadas de pantalla a coordenadas de imagen
        val points = floatArrayOf(screenX, screenY)
        inverseMatrix.mapPoints(points)
        
        val imageX = points[0]
        val imageY = points[1]
        
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "   Coordenadas en imagen: ($imageX, $imageY)")
        }
        
        // Para debug, NO rechazar si está fuera - normalizar con coerce
        // En producción, descomentar la validación
        // if (imageX < 0 || imageX > intrinsicWidth || imageY < 0 || imageY > intrinsicHeight) {
        //     return null
        // }
        
        // Normalizar a rango 0.0 - 1.0 (con coerce para debug)
        val normalizedX = (imageX / intrinsicWidth).coerceIn(0f, 1f)
        val normalizedY = (imageY / intrinsicHeight).coerceIn(0f, 1f)
        
        return PointF(normalizedX, normalizedY)
    }

    /**
     * Convierte coordenadas normalizadas a coordenadas de pantalla.
     * Útil para dibujar feedback visual.
     */
    private fun normalizedToScreenCoordinates(normalizedX: Float, normalizedY: Float): PointF? {
        val drawable = drawable ?: return null
        
        val intrinsicWidth = drawable.intrinsicWidth.toFloat()
        val intrinsicHeight = drawable.intrinsicHeight.toFloat()
        
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) return null
        
        // Convertir de normalizado a coordenadas de imagen
        val imageX = normalizedX * intrinsicWidth
        val imageY = normalizedY * intrinsicHeight
        
        // Aplicar matriz de transformación
        val points = floatArrayOf(imageX, imageY)
        imageMatrix.mapPoints(points)
        
        return PointF(points[0], points[1])
    }

    // ============ Búsqueda de Músculo ============

    /**
     * Encuentra el músculo más cercano a las coordenadas normalizadas dadas.
     * 
     * @param normalizedX Coordenada X normalizada (0.0 a 1.0)
     * @param normalizedY Coordenada Y normalizada (0.0 a 1.0)
     * @return El músculo más cercano dentro del radio de tolerancia, o null
     */
    private fun findNearestMusculo(normalizedX: Float, normalizedY: Float): Musculo? {
        var nearestMusculo: Musculo? = null
        var nearestDistance = Float.MAX_VALUE

        for (musculo in musculos) {
            val distance = hypot(
                (normalizedX - musculo.hotspotX).toDouble(),
                (normalizedY - musculo.hotspotY).toDouble()
            ).toFloat()

            if (distance < touchTolerance && distance < nearestDistance) {
                nearestDistance = distance
                nearestMusculo = musculo
            }
        }

        return nearestMusculo
    }

    // ============ Feedback Visual y Debug ============

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // DEBUG: Dibujar todos los hotspots de músculos (círculos rojos)
        if (debugMode && musculos.isNotEmpty()) {
            drawDebugHotspots(canvas)
        }
        
        // DEBUG: Dibujar punto de toque (círculo azul)
        if (debugMode && lastTouchScreenPoint != null) {
            drawDebugTouchPoint(canvas)
        }
        
        // Dibujar feedback visual si hay un músculo tocado
        if (showTouchFeedback && lastTouchedMusculo != null) {
            drawTouchFeedback(canvas)
        }
    }
    
    /**
     * DEBUG: Dibuja círculos rojos en la posición de cada hotspot de músculo.
     */
    private fun drawDebugHotspots(canvas: Canvas) {
        musculos.forEachIndexed { index, musculo ->
            val screenPos = normalizedToScreenCoordinates(musculo.hotspotX, musculo.hotspotY)
            if (screenPos != null) {
                // Círculo rojo relleno
                canvas.drawCircle(screenPos.x, screenPos.y, DEBUG_CIRCLE_RADIUS, debugHotspotPaint)
                // Borde
                canvas.drawCircle(screenPos.x, screenPos.y, DEBUG_CIRCLE_RADIUS, debugHotspotStrokePaint)
                
                // Número del músculo
                canvas.drawText(
                    "${index + 1}",
                    screenPos.x - 10,
                    screenPos.y + 10,
                    debugTextPaint
                )
                
                // Círculo de tolerancia (área de detección) - VERDE
                val toleranceRadius = touchTolerance * minOf(width, height)
                canvas.drawCircle(screenPos.x, screenPos.y, toleranceRadius, debugTolerancePaint)
            }
        }
        
        // Info de debug en esquina superior
        canvas.drawText("Músculos: ${musculos.size} | Tolerancia: $touchTolerance", 20f, 40f, debugTextPaint)
    }
    
    /**
     * DEBUG: Dibuja un círculo azul donde el usuario tocó.
     */
    private fun drawDebugTouchPoint(canvas: Canvas) {
        val touchPoint = lastTouchScreenPoint ?: return
        
        // Círculo azul grande
        canvas.drawCircle(touchPoint.x, touchPoint.y, DEBUG_CIRCLE_RADIUS * 1.5f, debugTouchPaint)
        canvas.drawCircle(touchPoint.x, touchPoint.y, DEBUG_CIRCLE_RADIUS * 1.5f, debugTouchStrokePaint)
        
        // Mostrar coordenadas normalizadas
        val normalizedPoint = lastTouchedPoint
        val coordText = if (normalizedPoint != null) {
            "Touch: (%.3f, %.3f)".format(normalizedPoint.x, normalizedPoint.y)
        } else {
            "Touch: (${touchPoint.x.toInt()}, ${touchPoint.y.toInt()})"
        }
        canvas.drawText(coordText, 20f, 80f, debugTextPaint)
        
        // Mostrar músculo detectado
        val musculoText = lastTouchedMusculo?.let { "🎯 Detectado: ${it.nombre}" } ?: "❌ No detectado"
        canvas.drawText(musculoText, 20f, 120f, debugTextPaint)
    }

    /**
     * Dibuja un indicador visual en la posición del músculo tocado.
     */
    private fun drawTouchFeedback(canvas: Canvas) {
        val musculo = lastTouchedMusculo ?: return
        
        // Obtener posición en pantalla del hotspot del músculo
        val screenPos = normalizedToScreenCoordinates(musculo.hotspotX, musculo.hotspotY) ?: return
        
        // Calcular radio del círculo de feedback (proporcional al tamaño de la vista)
        val feedbackRadius = minOf(width, height) * FEEDBACK_RADIUS_FACTOR
        
        // Dibujar círculo de fondo
        canvas.drawCircle(screenPos.x, screenPos.y, feedbackRadius, feedbackPaint)
        
        // Dibujar borde
        canvas.drawCircle(screenPos.x, screenPos.y, feedbackRadius, feedbackStrokePaint)
    }
}
