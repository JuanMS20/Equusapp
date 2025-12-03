package com.villalobos.caballoapp.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.villalobos.caballoapp.data.repository.QuizRepository
import com.villalobos.caballoapp.data.source.UserStats
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel para MainActivity.
 * Maneja la lógica de navegación principal y estado del tutorial.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    @Named("tutorial_prefs") private val sharedPreferences: SharedPreferences,
    private val quizRepository: QuizRepository
) : ViewModel() {

    companion object {
        private const val KEY_NO_MOSTRAR_TUTORIAL = "no_mostrar_tutorial"
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

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    // ============ Inicialización ============

    init {
        checkFirstTimeUser()
        loadUserStats()
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

    fun loadUserStats() {
        viewModelScope.launch {
            _userStats.value = quizRepository.getUserStats()
        }
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
