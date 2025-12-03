package com.villalobos.caballoapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.villalobos.caballoapp.ui.base.AccessibilityActivity
import com.villalobos.caballoapp.ui.accessibility.Accesibilidad
import com.villalobos.caballoapp.ui.credits.Creditos
import com.villalobos.caballoapp.ui.region.RegionMenu
import com.villalobos.caballoapp.ui.tutorial.TutorialActivity
import com.villalobos.caballoapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal que enlaza con todas las ventanas de la app.
 * Usa arquitectura MVVM con Hilt para inyección de dependencias.
 */
@AndroidEntryPoint
class MainActivity : AccessibilityActivity() {

    // MVVM: ViewModel inyectado con Hilt
    private val viewModel: MainViewModel by viewModels()
    
    private lateinit var binding: ActivityMainBinding
    
    override fun applyActivityAccessibilityColors() {
        // No hacer nada pesado aquí - el fondo ya se aplica en AccessibilityActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Configurar listeners usando el ViewModel
        binding.btnIniciar.setOnClickListener {
            viewModel.navigateToRegionMenu()
        }

        binding.btnAccesibilidad.setOnClickListener {
            viewModel.navigateToAccessibility()
        }

        binding.btnCreditos.setOnClickListener {
            viewModel.navigateToCredits()
        }

        binding.btnSalir.setOnClickListener {
            viewModel.exitApp()
        }
    }

    private fun observeViewModel() {
        // Observar eventos de navegación
        viewModel.event.observe(this) { event ->
            when (event) {
                is MainViewModel.MainEvent.NavigateToRegionMenu -> {
                    startActivity(Intent(this, RegionMenu::class.java))
                    viewModel.clearEvent()
                }
                is MainViewModel.MainEvent.NavigateToAccessibility -> {
                    startActivity(Intent(this, Accesibilidad::class.java))
                    viewModel.clearEvent()
                }
                is MainViewModel.MainEvent.NavigateToCredits -> {
                    startActivity(Intent(this, Creditos::class.java))
                    viewModel.clearEvent()
                }
                is MainViewModel.MainEvent.NavigateToTutorial -> {
                    startActivity(Intent(this, TutorialActivity::class.java))
                    viewModel.clearEvent()
                }
                is MainViewModel.MainEvent.ExitApp -> {
                    finishAffinity()
                }
                null -> { /* No action */ }
            }
        }

        // Observar si debe mostrar tutorial
            viewModel.shouldShowTutorial.observe(this) { shouldShow ->
            if (shouldShow) {
                viewModel.navigateToTutorial()
            }
        }
    }

    // Funciones legacy mantenidas por compatibilidad con XML onClick
    @Suppress("UNUSED_PARAMETER")
    fun btnIniciar(view: View) {
        viewModel.navigateToRegionMenu()
    }

    @Suppress("UNUSED_PARAMETER")
    fun btnAccesibilidad(view: View) {
        viewModel.navigateToAccessibility()
    }

    @Suppress("UNUSED_PARAMETER")
    fun btnCreditos(view: View) {
        viewModel.navigateToCredits()
    }

    @Suppress("UNUSED_PARAMETER")
    fun btnSalir(view: View) {
        viewModel.exitApp()
    }
}