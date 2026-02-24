package com.example.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de acesso aos resultados de quiz armazenados localmente.
 */
@Dao
interface QuizResultDao {
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY timestamp DESC")
    fun getResultsByUser(userId: String): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results ORDER BY percentage DESC, timeTakenSeconds ASC")
    fun getRanking(): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: QuizResultEntity)

    @Query("SELECT COUNT(*) FROM quiz_results WHERE userId = :userId")
    suspend fun getCountByUser(userId: String): Int

    @Query("SELECT AVG(percentage) FROM quiz_results WHERE userId = :userId")
    suspend fun getAverageByUser(userId: String): Double?

    @Query("SELECT MAX(percentage) FROM quiz_results WHERE userId = :userId")
    suspend fun getBestByUser(userId: String): Double?
}
