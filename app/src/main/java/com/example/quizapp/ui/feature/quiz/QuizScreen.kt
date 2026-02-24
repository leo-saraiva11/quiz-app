package com.example.quizapp.ui.feature.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.data.AppContainer
import com.example.quizapp.ui.theme.CorrectGreen
import com.example.quizapp.ui.theme.CorrectGreenLight
import com.example.quizapp.ui.theme.WrongRed
import com.example.quizapp.ui.theme.WrongRedLight

/**
 * Tela de execução do quiz com timer, progresso e feedback visual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizId: String,
    onQuizFinished: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = remember {
        QuizViewModel(
            quizId = quizId,
            quizRepository = AppContainer.quizRepository,
            resultRepository = AppContainer.resultRepository,
            authRepository = AppContainer.authRepository
        )
    }
) {
    val state by viewModel.uiState.collectAsState()

    // Navegar para resultado quando finalizar
    LaunchedEffect(state.isFinished, state.resultId) {
        if (state.isFinished && state.resultId != null) {
            onQuizFinished(state.resultId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.quizTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📚",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Voltar")
                        }
                    }
                }
            }
            else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Timer e progresso
                TimerAndProgress(state)

                Spacer(modifier = Modifier.height(24.dp))

                // Pergunta
                val question = state.questions[state.currentIndex]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Pergunta ${state.currentIndex + 1} de ${state.questions.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = question.question,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Opções
                question.options.forEach { option ->
                    val isSelected = state.selectedOption == option
                    val isCorrect = option == question.correct
                    val isAnswered = state.isAnswered

                    val backgroundColor by animateColorAsState(
                        targetValue = when {
                            !isAnswered -> MaterialTheme.colorScheme.surface
                            isCorrect -> CorrectGreenLight
                            isSelected && !isCorrect -> WrongRedLight
                            else -> MaterialTheme.colorScheme.surface
                        },
                        label = "optionBg"
                    )

                    val borderColor = when {
                        !isAnswered && isSelected -> MaterialTheme.colorScheme.primary
                        isAnswered && isCorrect -> CorrectGreen
                        isAnswered && isSelected && !isCorrect -> WrongRed
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    OutlinedCard(
                        onClick = { viewModel.selectOption(option) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = backgroundColor),
                        border = BorderStroke(
                            width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
                            color = borderColor
                        ),
                        enabled = !isAnswered
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            if (isAnswered) {
                                if (isCorrect) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Correto",
                                        tint = CorrectGreen
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        Icons.Default.Cancel,
                                        contentDescription = "Incorreto",
                                        tint = WrongRed
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botão avançar
                if (state.isAnswered && !state.isFinished) {
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (state.currentIndex + 1 >= state.questions.size) "Finalizar" else "Próxima",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } // end when
    }
}

@Composable
private fun TimerAndProgress(state: QuizUiState) {
    val remaining = state.totalTimeSeconds - state.elapsedSeconds
    val minutes = remaining / 60
    val seconds = remaining % 60
    val progress by animateFloatAsState(
        targetValue = if (state.totalTimeSeconds > 0)
            state.elapsedSeconds.toFloat() / state.totalTimeSeconds.toFloat()
        else 0f,
        label = "timer"
    )
    val timerColor = when {
        remaining <= 30 -> WrongRed
        remaining <= 60 -> Color(0xFFFF9800)
        else -> CorrectGreen
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Timer, contentDescription = null, tint = timerColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = timerColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        LinearProgressIndicator(
            progress = { 1f - progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = timerColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
