package com.example.quizapp.domain

/**
 * Classe selada que encapsula os possíveis estados de uma operação assíncrona.
 *
 * @param T Tipo do dado retornado em caso de sucesso.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
