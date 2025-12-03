package com.villalobos.caballoapp.ui.region

import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.ui.base.BaseRegionActivity
import com.villalobos.caballoapp.data.source.DatosMusculares
import com.villalobos.caballoapp.util.AccesibilityHelper
import com.villalobos.caballoapp.databinding.ActivityRegionCabezaBinding
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

class RegionCabeza : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionCabezaBinding

    override fun inflarLayout() {
        enlace = ActivityRegionCabezaBinding.inflate(layoutInflater)
        setContentView(enlace.root)
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