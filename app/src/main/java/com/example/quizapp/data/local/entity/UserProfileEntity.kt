package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que armazena o perfil do usuário localmente.
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val totalQuizzes: Int,
    val averageScore: Double,
    val bestScore: Double
)
