package com.example.quizapp.ui.feature.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.data.AppContainer
import com.example.quizapp.ui.theme.CorrectGreen
import com.example.quizapp.ui.theme.GradientEnd
import com.example.quizapp.ui.theme.GradientStart
import com.example.quizapp.ui.theme.WrongRed

/**
 * Tela de resultado com pontuação, percentual animado e tempo.
 */
@Composable
fun ResultScreen(
    resultId: String,
    onBackToDashboard: () -> Unit,
    onViewRanking: () -> Unit,
    viewModel: ResultViewModel = remember {
        ResultViewModel(
            resultId = resultId,
            resultRepository = AppContainer.resultRepository,
            userId = AppContainer.authRepository.getUserId() ?: ""
        )
    }
) {
    val result by viewModel.result.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        if (result == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            val r = result!!
            val emoji = when {
                r.percentage >= 80 -> "🏆"
                r.percentage >= 60 -> "😊"
                r.percentage >= 40 -> "🤔"
                else -> "📚"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = emoji, fontSize = 72.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when {
                        r.percentage >= 80 -> "Excelente!"
                        r.percentage >= 60 -> "Bom trabalho!"
                        r.percentage >= 40 -> "Pode melhorar"
                        else -> "Continue estudando!"
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = r.quizTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Circular progress
                var startAnimation by remember { mutableStateOf(false) }
                val animatedProgress by animateFloatAsState(
                    targetValue = if (startAnimation) (r.percentage / 100f).toFloat() else 0f,
                    animationSpec = tween(durationMillis = 1500),
                    label = "progress"
                )
                LaunchedEffect(Unit) { startAnimation = true }

                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progressColor = if (r.percentage >= 60) CorrectGreen else WrongRed
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 12.dp.toPx()
                        // Background arc
                        drawArc(
                            color = Color.White.copy(alpha = 0.2f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth)
                        )
                        // Progress arc
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${String.format("%.0f", r.percentage)}%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${r.score}/${r.totalQuestions}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stats Cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CorrectGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${r.score}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Acertos", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Cancel, contentDescription = null, tint = WrongRed)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${r.totalQuestions - r.score}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Erros", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            val min = r.timeTakenSeconds / 60
                            val sec = r.timeTakenSeconds % 60
                            Text(String.format("%02d:%02d", min, sec), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Tempo", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBackToDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Voltar ao Início", color = GradientStart, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onViewRanking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Ranking", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
