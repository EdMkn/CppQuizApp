package com.cppquiz.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for question database operations
 */
@Dao
interface QuestionDao {
    /**
     * Get all questions as a Flow (reactive)
     */
    @Query("SELECT * FROM questions ORDER BY id")
    fun getAllQuestions(): Flow<List<QuestionEntity>>
    
    /**
     * Get all questions synchronously (for initialization)
     */
    @Query("SELECT * FROM questions ORDER BY id")
    suspend fun getAllQuestionsSync(): List<QuestionEntity>
    
    /**
     * Get questions filtered by Java version as a Flow
     */
    @Query("SELECT * FROM questions WHERE languageVersion = :version ORDER BY id")
    fun getQuestionsByVersion(version: String): Flow<List<QuestionEntity>>

    /**
     * Get questions filtered by Java versions 
     */
    @Query("SELECT * FROM questions WHERE languageVersion IN (:versions) ORDER BY id")
    suspend fun getQuestionsByVersions(versions: List<String>): List<QuestionEntity>
    
    /**
     * Get a single question by ID
     */
    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Int): QuestionEntity?
    
    /**
     * Get distinct Java versions available in the database
     */
    @Query("SELECT DISTINCT languageVersion FROM questions ORDER BY CASE WHEN languageVersion = 'Core' THEN 0 WHEN languageVersion = '8' THEN 1 ELSE CAST(languageVersion AS INTEGER) END DESC")
    suspend fun getAvailableVersions(): List<String>
    
    /**
     * Get questions filtered by category as a Flow
     */
    @Query("SELECT * FROM questions WHERE category = :category ORDER BY id")
    fun getQuestionsByCategory(category: String): Flow<List<QuestionEntity>>
   
    /**
     * Get questions filtered by both version and category as a Flow
     */
    @Query("SELECT * FROM questions WHERE languageVersion = :version AND category = :category ORDER BY id")
    fun getQuestionsByVersionAndCategory(version: String, category: String): Flow<List<QuestionEntity>>
    
    @Query("SELECT * FROM questions WHERE languageVersion IN (:versions) AND category = :category")
    suspend fun getQuestionsByVersionsAndCategory(versions: List<String>, category: String): List<QuestionEntity>

    /**
     * Get distinct categories available in the database
     */
    @Query("SELECT DISTINCT category FROM questions ORDER BY category")
    suspend fun getAvailableCategories(): List<String>
    
    /**
     * Get distinct categories for a specific Java version
     */
    @Query("SELECT DISTINCT category FROM questions WHERE languageVersion = :version ORDER BY category")
    suspend fun getCategoriesByVersion(version: String): List<String>
    
    /**
     * Insert a single question
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)
    
    /**
     * Insert multiple questions (bulk insert)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
    
    /**
     * Delete all questions
     */
    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
    
    /**
     * Get total count of questions
     */
    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}



