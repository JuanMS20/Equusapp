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
import com.villalobos.caballoapp.util.ProgressionManager
import com.villalobos.caballoapp.databinding.ActivityRegionToracicaBinding
import com.villalobos.caballoapp.ui.detail.DetalleMusculo
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

class RegionToracica : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionToracicaBinding

    override fun inflarLayout() {
        enlace = ActivityRegionToracicaBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // configurarHotspotsZonas() is called in BaseRegionActivity.onCreate
    }

    override fun configurarHotspotsZonas() {
        enlace.hotspotZona4001.setOnClickListener { navegarADetalleZona(4001) }
        enlace.hotspotZona4002.setOnClickListener { navegarADetalleZona(4002) }
        enlace.hotspotZona4003.setOnClickListener { navegarADetalleZona(4003) }
        enlace.hotspotZona4004.setOnClickListener { navegarADetalleZona(4004) }
        enlace.hotspotZona4005.setOnClickListener { navegarADetalleZona(4005) }
        enlace.hotspotZona4006.setOnClickListener { navegarADetalleZona(4006) }
    }

    override fun getRegionImageView(): InteractiveAnatomyView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 4
}