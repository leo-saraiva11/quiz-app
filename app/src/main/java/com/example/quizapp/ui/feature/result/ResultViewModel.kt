package com.example.quizapp.ui.feature.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.ResultRepository
import com.example.quizapp.domain.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de resultado do quiz.
 */
class ResultViewModel(
    private val resultId: String,
    private val resultRepository: ResultRepository,
    private val userId: String
) : ViewModel() {

    private val _result = MutableStateFlow<QuizResult?>(null)
    val result = _result.asStateFlow()

    init {
        viewModelScope.launch {
            val results = resultRepository.getUserResults(userId).first()
            _result.value = results.find { it.id == resultId }
        }
    }
}
