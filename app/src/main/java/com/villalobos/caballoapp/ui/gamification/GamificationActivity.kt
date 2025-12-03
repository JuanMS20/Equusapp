package com.villalobos.caballoapp.ui.gamification

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.villalobos.caballoapp.databinding.ActivityGamificationBinding
import com.villalobos.caballoapp.ui.base.BaseNavigationActivity
import com.villalobos.caballoapp.ui.stats.StatsActivity
import com.villalobos.caballoapp.util.AccesibilityHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity para mostrar el progreso de gamificación del usuario.
 * Muestra XP, rango, racha y medallas desbloqueadas.
 */
@AndroidEntryPoint
class GamificationActivity : BaseNavigationActivity() {

    private lateinit var binding: ActivityGamificationBinding
    private val viewModel: GamificationViewModel by viewModels()
    private lateinit var badgeAdapter: BadgeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        observeViewModel()
        setupHomeButton(binding.btnHome)
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnViewStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        badgeAdapter = BadgeAdapter()
        binding.rvBadges.apply {
            layoutManager = GridLayoutManager(this@GamificationActivity, 3)
            adapter = badgeAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userProgress.collect { progress ->
                        progress?.let { updateProgressUI(it) }
                    }
                }
                launch {
                    viewModel.badges.collect { badges ->
                        badgeAdapter.submitList(badges)
                        updateBadgeCount(badges.count { it.isUnlocked }, badges.size)
                    }
                }
            }
        }
    }

    private fun updateProgressUI(progress: com.villalobos.caballoapp.data.model.UserProgressEntity) {
        binding.apply {
            tvCurrentXp.text = "${progress.currentXp} XP"
            tvCurrentRank.text = progress.currentRank
            tvStudyStreak.text = "${progress.studyStreak} días"
            
            // Calcular progreso hacia el siguiente rango
            val (currentLevelXp, nextLevelXp) = viewModel.getXpRangeForRank(progress.currentRank)
            val progressPercent = if (nextLevelXp > currentLevelXp) {
                ((progress.currentXp - currentLevelXp).toFloat() / (nextLevelXp - currentLevelXp) * 100).toInt()
            } else {
                100
            }
            progressBar.progress = progressPercent.coerceIn(0, 100)
            tvProgressPercent.text = "$progressPercent%"
        }
    }

    private fun updateBadgeCount(unlocked: Int, total: Int) {
        binding.tvBadgeCount.text = "$unlocked / $total medallas"
    }

    override fun applyActivityAccessibilityColors() {
        try {
            val config = AccesibilityHelper.getAccessibilityConfig(this)
            AccesibilityHelper.applyBackgroundGradient(this, window.decorView, config.colorblindType)
        } catch (_: Exception) { }
    }
}
