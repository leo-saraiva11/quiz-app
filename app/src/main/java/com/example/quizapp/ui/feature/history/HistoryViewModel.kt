package com.example.quizapp.ui.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.data.remote.ResultRepository
import com.example.quizapp.domain.QuizResult
import com.example.quizapp.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de histórico pessoal.
 */
class HistoryViewModel(
    private val resultRepository: ResultRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId = authRepository.getUserId() ?: ""

    val results = resultRepository.getUserResults(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _stats = MutableStateFlow(UserProfile())
    val stats = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            _stats.value = resultRepository.getUserStats(userId)
        }
    }
}
