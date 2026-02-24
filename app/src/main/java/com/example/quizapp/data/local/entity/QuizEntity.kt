package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa um quiz armazenado localmente.
 */
@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val timeMinutes: Int,
    val questionCount: Int,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
