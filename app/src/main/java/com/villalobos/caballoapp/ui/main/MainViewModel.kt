package com.villalobos.caballoapp.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel para MainActivity.
 * Maneja la lógica de navegación principal y estado del tutorial.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    @Named("tutorial_prefs") private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_NO_MOSTRAR_TUTORIAL = "no_mostrar_tutorial"
        private const val KEY_USER_LEVEL = "user_level"
        private const val KEY_USER_XP = "user_xp"
        private const val KEY_USER_STREAK = "user_streak"
    }

    // ============ Estados ============

    sealed class MainEvent {
        object NavigateToRegionMenu : MainEvent()
        object NavigateToAccessibility : MainEvent()
        object NavigateToCredits : MainEvent()
        object NavigateToTutorial : MainEvent()
        object ExitApp : MainEvent()
    }

    // ============ LiveData ============

    private val _event = MutableLiveData<MainEvent?>()
    val event: LiveData<MainEvent?> = _event

    private val _shouldShowTutorial = MutableLiveData<Boolean>()
    val shouldShowTutorial: LiveData<Boolean> = _shouldShowTutorial

    // Stats del usuario
    private val _userLevel = MutableLiveData<Int>(1)
    val userLevel: LiveData<Int> = _userLevel

    private val _userXp = MutableLiveData<Int>(0)
    val userXp: LiveData<Int> = _userXp

    private val _userStreak = MutableLiveData<Int>(0)
    val userStreak: LiveData<Int> = _userStreak

    // ============ Inicialización ============

    init {
        checkFirstTimeUser()
        loadUserStats()
    }

    private fun loadUserStats() {
        _userLevel.value = sharedPreferences.getInt(KEY_USER_LEVEL, 1)
        _userXp.value = sharedPreferences.getInt(KEY_USER_XP, 0)
        _userStreak.value = sharedPreferences.getInt(KEY_USER_STREAK, 0)
    }

    // ============ Acciones ============

    /**
     * Verifica si es la primera vez que el usuario abre la app.
     */
    fun checkFirstTimeUser() {
        val noMostrarTutorial = sharedPreferences.getBoolean(KEY_NO_MOSTRAR_TUTORIAL, false)
        _shouldShowTutorial.value = !noMostrarTutorial
    }

    /**
     * Marca que el tutorial no debe mostrarse de nuevo.
     */
    fun markTutorialAsShown() {
        sharedPreferences.edit()
            .putBoolean(KEY_NO_MOSTRAR_TUTORIAL, true)
            .apply()
        _shouldShowTutorial.value = false
    }

    /**
     * Reinicia el tutorial para mostrarlo de nuevo.
     */
    fun resetTutorial() {
        sharedPreferences.edit()
            .putBoolean(KEY_NO_MOSTRAR_TUTORIAL, false)
            .apply()
        _shouldShowTutorial.value = true
    }

    // ============ Navegación ============

    fun navigateToRegionMenu() {
        _event.value = MainEvent.NavigateToRegionMenu
    }

    fun navigateToAccessibility() {
        _event.value = MainEvent.NavigateToAccessibility
    }

    fun navigateToCredits() {
        _event.value = MainEvent.NavigateToCredits
    }

    fun navigateToTutorial() {
        _event.value = MainEvent.NavigateToTutorial
    }

    fun exitApp() {
        _event.value = MainEvent.ExitApp
    }

    /**
     * Limpia el evento actual (después de ser consumido).
     */
    fun clearEvent() {
        _event.value = null
    }

    // ============ Helpers ============

    /**
     * Verifica si el tutorial está habilitado.
     */
    fun isTutorialEnabled(): Boolean {
        return !sharedPreferences.getBoolean(KEY_NO_MOSTRAR_TUTORIAL, false)
    }
}
