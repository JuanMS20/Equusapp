package com.villalobos.caballoapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.databinding.ActivityStatsBinding
import com.villalobos.caballoapp.ui.base.BaseNavigationActivity
import com.villalobos.caballoapp.util.AccesibilityHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity para mostrar estadísticas detalladas del usuario.
 */
@AndroidEntryPoint
class StatsActivity : BaseNavigationActivity() {

    private lateinit var binding: ActivityStatsBinding
    private val viewModel: StatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        setupHomeButton(binding.btnHome)
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stats.collect { stats ->
                    stats?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(stats: StatsViewModel.StatsData) {
        binding.apply {
            // Quiz Stats
            tvTotalQuizzes.text = stats.totalQuizzes.toString()
            tvBestScore.text = "${stats.bestScore}%"
            tvPerfectQuizzes.text = "${stats.perfectQuizzes} ⭐"
            tvFastestTime.text = formatTime(stats.fastestTime)

            // Study Stats
            tvMusclesStudied.text = "${stats.musclesStudied} / ${stats.totalMuscles}"
            tvStudyStreak.text = if (stats.studyStreak > 0) "🔥 ${stats.studyStreak} días" else "0 días"

            // Region Scores
            updateRegionScores(stats.regionScores)
        }
    }

    private fun updateRegionScores(regionScores: Map<String, Int>) {
        binding.regionScoresContainer.removeAllViews()
        
        val regions = listOf(
            "Cabeza" to 1,
            "Cuello" to 2,
            "Tronco" to 3,
            "Miembros Torácicos" to 4,
            "Miembros Pélvicos" to 5
        )

        for ((regionName, regionId) in regions) {
            val score = regionScores[regionId.toString()] ?: 0
            addRegionScoreView(regionName, score)
        }
    }

    private fun addRegionScoreView(regionName: String, score: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_region_score, binding.regionScoresContainer, false)
        
        view.findViewById<TextView>(R.id.tvRegionName).text = regionName
        view.findViewById<TextView>(R.id.tvRegionScore).text = "$score%"
        view.findViewById<ProgressBar>(R.id.progressRegion).progress = score
        
        binding.regionScoresContainer.addView(view)
    }

    private fun formatTime(timeMs: Long): String {
        if (timeMs == Long.MAX_VALUE) return "--:--"
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun applyActivityAccessibilityColors() {
        try {
            val config = AccesibilityHelper.getAccessibilityConfig(this)
            AccesibilityHelper.applyBackgroundGradient(this, window.decorView, config.colorblindType)
        } catch (_: Exception) { }
    }
}
