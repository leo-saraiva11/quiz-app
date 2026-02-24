package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que armazena o resultado de uma sessão de quiz.
 */
@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val quizTitle: String,
    val userId: String,
    val userName: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Double,
    val timeTakenSeconds: Long,
    val timestamp: Long
)
