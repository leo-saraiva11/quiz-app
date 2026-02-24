package com.example.quizapp.ui.feature.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.data.remote.ResultRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para a tela de ranking global.
 */
class RankingViewModel(
    resultRepository: ResultRepository,
    val authRepository: AuthRepository
) : ViewModel() {

    val ranking = resultRepository.getRanking()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserId: String
        get() = authRepository.getUserId() ?: ""
}
