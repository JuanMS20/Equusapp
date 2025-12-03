package com.villalobos.caballoapp.ui.achievements

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.villalobos.caballoapp.databinding.ActivityAchievementsBinding
import com.villalobos.caballoapp.ui.base.AccessibilityActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AchievementsActivity : AccessibilityActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private val viewModel: AchievementsViewModel by viewModels()
    private val adapter = AchievementsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.achievements.observe(this) { items ->
            adapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun applyActivityAccessibilityColors() {
        // Implementar lógica de colores de accesibilidad si es necesario
    }
}
