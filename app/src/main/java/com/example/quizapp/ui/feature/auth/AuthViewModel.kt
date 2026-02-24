package com.example.quizapp.ui.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel compartilhado para login e cadastro.
 * Gerencia o estado de autenticação e expõe resultados via StateFlow.
 */
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Result.Loading
            _authState.value = repository.login(email, password)
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = Result.Loading
            _authState.value = repository.signUp(email, password, displayName)
        }
    }

    fun resetState() {
        _authState.value = null
    }
}
