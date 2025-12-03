package com.villalobos.caballoapp.ui.region

import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.ui.base.BaseRegionActivity
import com.villalobos.caballoapp.databinding.ActivityRegionCuelloBinding
import com.villalobos.caballoapp.ui.components.InteractiveAnatomyView

class RegionCuello : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionCuelloBinding

    override fun inflarLayout() {
        enlace = ActivityRegionCuelloBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun getRegionImageView(): InteractiveAnatomyView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 2
}