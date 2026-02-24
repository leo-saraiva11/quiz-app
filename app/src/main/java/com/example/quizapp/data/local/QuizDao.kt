package com.example.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entity.QuizEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de acesso aos quizzes armazenados localmente.
 */
@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): Flow<List<QuizEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quizzes: List<QuizEntity>)

    @Query("DELETE FROM quizzes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM quizzes")
    suspend fun getCount(): Int
}
