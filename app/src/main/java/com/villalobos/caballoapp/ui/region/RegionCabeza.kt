package com.villalobos.caballoapp.ui.region

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.ui.base.BaseRegionActivity
import com.villalobos.caballoapp.data.source.DatosMusculares
import com.villalobos.caballoapp.util.AccesibilityHelper
import com.villalobos.caballoapp.databinding.ActivityRegionCabezaBinding
import com.villalobos.caballoapp.ui.detail.DetalleMusculo
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView
import com.villalobos.caballoapp.util.ProgressionManager

class RegionCabeza : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionCabezaBinding

    override fun inflarLayout() {
        enlace = ActivityRegionCabezaBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // configurarHotspotsZonas() is called in BaseRegionActivity.onCreate
    }

    /**
     * Configura los listeners de las zonas táctiles (hotspots).
     * Cada hotspot abre el detalle de la zona correspondiente.
     */
    override fun configurarHotspotsZonas() {
        // Zona 1001: Parieto-Temporal
        enlace.hotspotZona1001.setOnClickListener { navegarADetalleZona(1001) }
        
        // Zona 1002: Auricular
        enlace.hotspotZona1002.setOnClickListener { navegarADetalleZona(1002) }
        
        // Zona 1003: Orbital
        enlace.hotspotZona1003.setOnClickListener { navegarADetalleZona(1003) }
        
        // Zona 1004: Mentoniana
        enlace.hotspotZona1004.setOnClickListener { navegarADetalleZona(1004) }
        
        // Zona 1005: Maxilar
        enlace.hotspotZona1005.setOnClickListener { navegarADetalleZona(1005) }
        
        // Zona 1006: Mandibular
        enlace.hotspotZona1006.setOnClickListener { navegarADetalleZona(1006) }
        
        // Zona 1007: Mesetérica
        enlace.hotspotZona1007.setOnClickListener { navegarADetalleZona(1007) }
        
        // Zona 1008: Bucal
        enlace.hotspotZona1008.setOnClickListener { navegarADetalleZona(1008) }
    }

    override fun getRegionImageView(): InteractiveAnatomyView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 1

    override fun configurarDescripcionesRegion() {
        super.configurarDescripcionesRegion()
        
        val region = DatosMusculares.obtenerRegionPorId(regionId)
        region?.let {
            // Configurar instrucciones adicionales específicas de la cabeza
            AccesibilityHelper.setContentDescription(
                enlace.tvInstructions,
                "Instrucciones: Selecciona un músculo de la lista para ver información detallada",
                "Instrucción"
            )

            AccesibilityHelper.setContentDescription(
                enlace.tvSubInstructions,
                "Sugerencia: Puedes navegar por los músculos usando gestos de deslizamiento o navegación por teclado",
                "Ayuda"
            )
        }
    }
}