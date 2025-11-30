package com.villalobos.caballoapp

import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.villalobos.caballoapp.databinding.ActivityRegionTroncoBinding

class RegionTronco : BaseRegionActivity() {

    private lateinit var enlace: ActivityRegionTroncoBinding

    override fun inflarLayout() {
        enlace = ActivityRegionTroncoBinding.inflate(layoutInflater)
        setContentView(enlace.root)
    }

    override fun getRegionImageView(): ImageView = enlace.imgRegion
    override fun getTitleTextView(): TextView = enlace.tvTitle
    override fun getMusclesRecyclerView(): RecyclerView = enlace.rvMuscles
    override fun getHomeButton(): ImageButton = enlace.btnHome
    override fun getQuizButton(): Button = enlace.btnQuizRegion
    override fun getDefaultRegionId(): Int = 3
}