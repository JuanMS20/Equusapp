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
import com.villalobos.caballoapp.databinding.ActivityRegionTroncoBinding
import com.villalobos.caballoapp.ui.detail.DetalleMusculo
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

class RegionTronco : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionTroncoBinding

    override fun inflarLayout() {
        enlace = ActivityRegionTroncoBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // configurarHotspotsZonas() is called in BaseRegionActivity.onCreate
    }

    override fun configurarHotspotsZonas() {
        enlace.hotspotZona3001.setOnClickListener { navegarADetalleZona(3001) }
        enlace.hotspotZona3002.setOnClickListener { navegarADetalleZona(3002) }
        enlace.hotspotZona3003.setOnClickListener { navegarADetalleZona(3003) }
        enlace.hotspotZona3004.setOnClickListener { navegarADetalleZona(3004) }
        enlace.hotspotZona3005.setOnClickListener { navegarADetalleZona(3005) }
        enlace.hotspotZona3006.setOnClickListener { navegarADetalleZona(3006) }
        enlace.hotspotZona3007.setOnClickListener { navegarADetalleZona(3007) }
        enlace.hotspotZona3008.setOnClickListener { navegarADetalleZona(3008) }
        enlace.hotspotZona3009.setOnClickListener { navegarADetalleZona(3009) }
    }

    override fun getRegionImageView(): InteractiveAnatomyView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 3
}