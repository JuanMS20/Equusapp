package com.villalobos.caballoapp.ui.region

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.ui.base.BaseRegionActivity
import com.villalobos.caballoapp.databinding.ActivityRegionDistalBinding
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

class RegionDistal : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionDistalBinding

    override fun inflarLayout() {
        enlace = ActivityRegionDistalBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun getRegionImageView(): InteractiveAnatomyView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 6

    override fun configurarHotspotsZonas() {
        // Mapa de zonas: ID de vista -> ID de zona
        val mapaZonas = mapOf(
            enlace.hotspotZona6001 to 6001, // Vista Palmar
            enlace.hotspotZona6002 to 6002  // Vista Lateral
        )

        mapaZonas.forEach { (vista, zonaId) ->
            vista.setOnClickListener {
                navegarADetalleZona(zonaId)
            }
        }
    }
}