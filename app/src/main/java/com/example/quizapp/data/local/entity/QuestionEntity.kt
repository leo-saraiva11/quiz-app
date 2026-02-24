package com.example.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa uma questão armazenada localmente.
 * O campo [options] é armazenado como String separada por "|||".
 */
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val question: String,
    val options: String,
    val correct: String
)
