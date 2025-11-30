package com.villalobos.caballoapp

import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.ui.region.RegionViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Clase base abstracta para todas las actividades de región.
 * Proporciona funcionalidad común para mostrar músculos, hotspots y navegación.
 * Usa arquitectura MVVM con Hilt para inyección de dependencias.
 */
@AndroidEntryPoint
abstract class BaseRegionActivity : AccessibilityActivity() {

    // MVVM: ViewModel inyectado con Hilt
    protected val regionViewModel: RegionViewModel by viewModels()
    
    protected lateinit var adaptadorMusculos: AdaptadorMusculos
    protected var musculos: List<Musculo> = emptyList()
    protected var regionId: Int = 0

    // Vistas abstractas que deben ser proporcionadas por las subclases
    protected abstract fun getRegionImageView(): ImageView
    protected abstract fun getTitleTextView(): TextView
    protected abstract fun getMusclesRecyclerView(): RecyclerView
    protected abstract fun getHomeButton(): ImageButton
    protected abstract fun getQuizButton(): Button
    protected abstract fun getDefaultRegionId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Inflar el layout específico de la actividad
            inflarLayout()

            // Obtener el ID de la región del intent con validación
            obtenerParametrosIntent()

            // Cargar datos usando ViewModel
            regionViewModel.loadRegion(regionId)

            // Observar ViewModel
            observeViewModel()

            // Configurar hotspots cuando la imagen esté lista
            configurarHotspots()

            // Configurar botones de navegación
            configurarBotonesNavegacion()

        } catch (e: Exception) {
            ErrorHandler.handleError(
                context = this,
                throwable = e,
                errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
                userMessage = "Error al inicializar la región",
                canRecover = true,
                recoveryAction = { finish() }
            )
        }
    }

    /**
     * Método abstracto que debe inflar el layout específico de la actividad.
     */
    protected abstract fun inflarLayout()

    protected open fun obtenerParametrosIntent() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.INTENT_ERROR,
            errorMessage = "Error al obtener parámetros de navegación"
        ) {
            regionId = intent.getIntExtra("REGION_ID", getDefaultRegionId())

            if (regionId <= 0) {
                throw IllegalArgumentException("ID de región inválido: $regionId")
            }
        }
    }

    protected open fun observeViewModel() {
        // Observar estado
        regionViewModel.state.observe(this) { state ->
            state.region?.let { region ->
                getTitleTextView().text = region.nombreCompleto.uppercase()
            }

            if (state.muscles.isNotEmpty() && state.muscles != musculos) {
                musculos = state.muscles
                configurarRecyclerView()
                
                // Animar la imagen de la región
                ImageAnimationHelper.animateRegionImage(getRegionImageView())
            }

            state.error?.let { error ->
                ErrorHandler.handleError(
                    context = this,
                    throwable = Exception(error),
                    errorType = ErrorHandler.ErrorType.DATA_LOADING_ERROR,
                    userMessage = error,
                    canRecover = true,
                    recoveryAction = { finish() }
                )
            }
        }

        // Observar eventos
        regionViewModel.event.observe(this) { event ->
            when (event) {
                is RegionViewModel.RegionEvent.NavigateToDetail -> {
                    irADetalleMusculo(event.muscle)
                    regionViewModel.clearEvent()
                }
                is RegionViewModel.RegionEvent.MuscleSelected -> {
                    // Opcional: animar o resaltar músculo seleccionado
                    regionViewModel.clearEvent()
                }
                is RegionViewModel.RegionEvent.Error -> {
                    ErrorHandler.handleError(
                        context = this,
                        throwable = Exception(event.message),
                        errorType = ErrorHandler.ErrorType.DATA_LOADING_ERROR,
                        userMessage = event.message,
                        canRecover = true
                    )
                    regionViewModel.clearEvent()
                }
                null -> { /* No action */ }
            }
        }
    }

    protected open fun configurarRecyclerView() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al configurar lista de músculos"
        ) {
            adaptadorMusculos = AdaptadorMusculos(musculos) { musculo ->
                regionViewModel.navigateToMuscleDetail(musculo)
            }

            getMusclesRecyclerView().layoutManager = LinearLayoutManager(this)
            getMusclesRecyclerView().adapter = adaptadorMusculos

            // Aplicar configuración de accesibilidad visual
            aplicarAccesibilidadVisual()
        }
    }

    protected open fun configurarHotspots() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al configurar hotspots"
        ) {
            getRegionImageView().viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        getRegionImageView().viewTreeObserver.removeOnGlobalLayoutListener(this)
                        // Crear hotspots dinámicos
                        val container = getRegionImageView().parent as? RelativeLayout ?: return
                        HotspotHelper.crearHotspots(
                            context = this@BaseRegionActivity,
                            container = container,
                            imageView = getRegionImageView(),
                            musculos = musculos,
                            onClick = { musculo -> 
                                regionViewModel.navigateToMuscleDetail(musculo)
                            }
                        )
                    } catch (e: Exception) {
                        ErrorHandler.handleError(
                            context = this@BaseRegionActivity,
                            throwable = e,
                            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
                            level = ErrorHandler.ErrorLevel.WARNING,
                            userMessage = "Error en configuración visual",
                            canRecover = true
                        )
                    }
                }
            })
        }
    }

    protected open fun irADetalleMusculo(musculo: Musculo) {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.NAVIGATION_ERROR,
            errorMessage = "Error al navegar al detalle del músculo"
        ) {
            // Validar músculo antes de navegar
            if (!ErrorHandler.validarMusculo(musculo)) {
                throw IllegalArgumentException("Datos de músculo inválidos para navegación")
            }

            val intent = Intent(this, DetalleMusculo::class.java).apply {
                putExtra("MUSCULO_ID", musculo.id)
                putExtra("REGION_ID", regionId)
            }

            startActivity(intent)
        }
    }

    protected open fun configurarBotonesNavegacion() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al configurar botones de navegación"
        ) {
            // Configurar botón de home usando la clase base
            setupHomeButton(getHomeButton())

            // Configurar listener del botón de quiz
            getQuizButton().setOnClickListener {
                irAQuizRegion()
            }
        }
    }

    protected open fun irAQuizRegion() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.NAVIGATION_ERROR,
            errorMessage = "Error al navegar al quiz de la región"
        ) {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("REGION_ID", regionId)
            startActivity(intent)
        }
    }

    override fun applyActivityAccessibilityColors() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al aplicar colores de accesibilidad"
        ) {
            AccesibilityHelper.applySpecificColorblindColors(
                this,
                window.decorView,
                AccesibilityHelper.getAccessibilityConfig(this).colorblindType
            )
        }
    }

    override fun onResume() {
        super.onResume()

        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al reanudar actividad"
        ) {
            // Verificar que los datos sigan siendo válidos
            if (musculos.isEmpty()) {
                regionViewModel.loadRegion(regionId)
            }
        }
    }

    protected open fun aplicarAccesibilidadVisual() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al aplicar accesibilidad visual"
        ) {
            // Configurar accesibilidad del dispositivo
            AccesibilityHelper.setupDeviceAccessibility(this, window.decorView)

            // Configurar descripciones específicas para la región
            configurarDescripcionesRegion()

            // Configurar imagen anatómica con descripción
            val regionName = regionViewModel.getRegionName()
            AccesibilityHelper.setAnatomicalImageDescription(
                getRegionImageView(),
                regionName,
                musculos.size
            )

            // Habilitar navegación por teclado
            AccesibilityHelper.enableKeyboardNavigation(
                getRegionImageView(),
                getMusclesRecyclerView()
            )
        }
    }

    protected open fun configurarDescripcionesRegion() {
        val regionName = regionViewModel.getRegionName()
        
        // Configurar título de la región
        AccesibilityHelper.setContentDescription(
            getTitleTextView(),
            "Título de región: $regionName",
            "Encabezado"
        )

        // Configurar lista de músculos
        AccesibilityHelper.setContentDescription(
            getMusclesRecyclerView(),
            "Lista de ${musculos.size} músculos en $regionName. " +
                    "Cada elemento muestra nombre, descripción y función del músculo",
            "Lista de músculos"
        )
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
        } catch (e: Exception) {
            ErrorHandler.handleError(
                context = this,
                throwable = e,
                errorType = ErrorHandler.ErrorType.MEMORY_ERROR,
                level = ErrorHandler.ErrorLevel.WARNING,
                userMessage = "Error al liberar recursos",
                canRecover = false
            )
        }
    }
}
