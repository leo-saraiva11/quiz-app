package com.example.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.quizapp.ui.feature.auth.LoginScreen
import com.example.quizapp.ui.feature.auth.SignUpScreen
import com.example.quizapp.ui.feature.dashboard.DashboardScreen
import com.example.quizapp.ui.feature.history.HistoryScreen
import com.example.quizapp.ui.feature.quiz.QuizScreen
import com.example.quizapp.ui.feature.ranking.RankingScreen
import com.example.quizapp.ui.feature.result.ResultScreen

/**
 * Grafo de navegação principal do aplicativo.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any = LoginRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(DashboardRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(SignUpRoute)
                }
            )
        }

        composable<SignUpRoute> {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(DashboardRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<DashboardRoute> {
            DashboardScreen(
                onQuizClick = { quizId ->
                    navController.navigate(QuizRoute(quizId))
                },
                onHistoryClick = {
                    navController.navigate(HistoryRoute)
                },
                onRankingClick = {
                    navController.navigate(RankingRoute)
                },
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(DashboardRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<QuizRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<QuizRoute>()
            QuizScreen(
                quizId = route.quizId,
                onQuizFinished = { resultId ->
                    navController.navigate(ResultRoute(resultId)) {
                        popUpTo(DashboardRoute)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<ResultRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ResultRoute>()
            ResultScreen(
                resultId = route.resultId,
                onBackToDashboard = {
                    navController.navigate(DashboardRoute) {
                        popUpTo(DashboardRoute) { inclusive = true }
                    }
                },
                onViewRanking = {
                    navController.navigate(RankingRoute)
                }
            )
        }

        composable<HistoryRoute> {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<RankingRoute> {
            RankingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
