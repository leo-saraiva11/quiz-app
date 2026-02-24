package com.example.quizapp.ui.feature.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.quizapp.domain.QuizResult
import com.example.quizapp.ui.theme.BronzeMedal
import com.example.quizapp.ui.theme.GoldMedal
import com.example.quizapp.ui.theme.SilverMedal

/**
 * Tela de ranking global com top 3 destacado e posição do usuário.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onNavigateBack: () -> Unit,
    viewModel: RankingViewModel = remember {
        RankingViewModel(AppContainer.resultRepository, AppContainer.authRepository)
    }
) {
    val ranking by viewModel.ranking.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking Global") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (ranking.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nenhum resultado no ranking ainda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(ranking) { index, result ->
                    RankingItem(
                        position = index + 1,
                        result = result,
                        isCurrentUser = result.userId == viewModel.currentUserId
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingItem(position: Int, result: QuizResult, isCurrentUser: Boolean) {
    val medalColor = when (position) {
        1 -> GoldMedal
        2 -> SilverMedal
        3 -> BronzeMedal
        else -> null
    }
    val medalEmoji = when (position) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> null
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isCurrentUser)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            if (medalEmoji != null) {
                Text(text = medalEmoji, fontSize = 28.sp)
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$position",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = result.userName.ifBlank { "Anônimo" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                "Você",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                Text(
                    text = result.quizTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.0f", result.percentage)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = medalColor ?: MaterialTheme.colorScheme.primary
                )
                val min = result.timeTakenSeconds / 60
                val sec = result.timeTakenSeconds % 60
                Text(
                    text = String.format("%02d:%02d", min, sec),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
