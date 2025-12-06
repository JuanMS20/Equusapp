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
import com.villalobos.caballoapp.databinding.ActivityRegionCuelloBinding
import com.villalobos.caballoapp.ui.detail.DetalleMusculo
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

class RegionCuello : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionCuelloBinding

    override fun inflarLayout() {
        enlace = ActivityRegionCuelloBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // configurarHotspotsZonas() is called in BaseRegionActivity.onCreate
    }

    override fun configurarHotspotsZonas() {
        enlace.hotspotZona2001.setOnClickListener { navegarADetalleZona(2001) }
        enlace.hotspotZona2002.setOnClickListener { navegarADetalleZona(2002) }
        enlace.hotspotZona2003.setOnClickListener { navegarADetalleZona(2003) }
        enlace.hotspotZona2004.setOnClickListener { navegarADetalleZona(2004) }
        enlace.hotspotZona2005.setOnClickListener { navegarADetalleZona(2005) }
        enlace.hotspotZona2006.setOnClickListener { navegarADetalleZona(2006) }
    }

    override fun getRegionImageView(): InteractiveAnatomyView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 2
}