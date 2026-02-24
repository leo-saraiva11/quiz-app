package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.data.AppContainer
import com.example.quizapp.navigation.AppNavHost
import com.example.quizapp.navigation.DashboardRoute
import com.example.quizapp.navigation.LoginRoute
import com.example.quizapp.ui.theme.QuizAppTheme
import kotlinx.coroutines.flow.first

/**
 * Activity principal do aplicativo.
 * Observa o estado de autenticação para decidir a tela inicial.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Verificar se já existe um utilizador autenticado
                    val isLoggedIn = AppContainer.authRepository.getUserId() != null

                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) DashboardRoute else LoginRoute
                    )
                }
            }
        }
    }
}
