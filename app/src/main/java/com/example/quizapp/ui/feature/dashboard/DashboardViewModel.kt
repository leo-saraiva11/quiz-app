package com.example.quizapp.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.data.remote.QuizRepository
import com.example.quizapp.data.remote.ResultRepository
import com.example.quizapp.domain.Quiz
import com.example.quizapp.domain.Result
import com.example.quizapp.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel do Dashboard.
 * Carrega quizzes disponíveis, estatísticas do usuário, e gerencia sincronização.
 */
class DashboardViewModel(
    private val quizRepository: QuizRepository,
    private val resultRepository: ResultRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val quizzes = quizRepository.getQuizzes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userStats = MutableStateFlow(UserProfile())
    val userStats = _userStats.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage = _syncMessage.asStateFlow()

    val userName: String
        get() = authRepository.getUserDisplayName() ?: "Usuário"

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Sincronizar questões se não há dados locais
            if (!quizRepository.hasLocalData()) {
                syncQuizzes()
            }
            // Carregar estatísticas do usuário
            val userId = authRepository.getUserId() ?: return@launch
            _userStats.value = resultRepository.getUserStats(userId)
            // Sincronizar resultados do Firebase
            resultRepository.syncResultsFromFirebase(userId)
            _userStats.value = resultRepository.getUserStats(userId)
        }
    }

    fun syncQuizzes() {
        viewModelScope.launch {
            _isSyncing.value = true
            when (val result = quizRepository.syncFromFirebase()) {
                is Result.Success -> {
                    _syncMessage.value = "Questões atualizadas com sucesso!"
                }
                is Result.Error -> {
                    _syncMessage.value = "Erro ao sincronizar: ${result.exception.message}"
                }
                is Result.Loading -> {}
            }
            _isSyncing.value = false
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    fun logout() {
        authRepository.logout()
    }
}
