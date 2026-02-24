package com.example.quizapp.navigation

import kotlinx.serialization.Serializable

/**
 * Rotas de navegação type-safe do aplicativo.
 */
@Serializable object LoginRoute
@Serializable object SignUpRoute
@Serializable object DashboardRoute
@Serializable data class QuizRoute(val quizId: String)
@Serializable data class ResultRoute(val resultId: String)
@Serializable object HistoryRoute
@Serializable object RankingRoute
