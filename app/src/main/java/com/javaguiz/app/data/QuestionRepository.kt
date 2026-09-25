package com.javaguiz.app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList

/**
 * Repository for question data access
 * Provides a clean API for accessing questions from the database
 */
class QuestionRepository(context: Context) {
    private val database = QuizDatabase.getDatabase(context)
    private val questionDao = database.questionDao()
    
    /**
     * Get all questions as a Flow
     */
    fun getAllQuestions(): Flow<List<Question>> {
        return questionDao.getAllQuestions().map { entities ->
            entities.map { it.toQuestion() }
        }
    }
    
    /**
     * Get all questions synchronously
     */
    suspend fun getAllQuestionsSync(): List<Question> {
        return questionDao.getAllQuestionsSync().map { it.toQuestion() }
    }
    
    /**
     * Get questions filtered by Java version as a Flow
     */
    fun getQuestionsByVersion(version: String?): Flow<List<Question>> {
        return if (version == null || version == "All") {
            getAllQuestions()
        } else {
            questionDao.getQuestionsByVersion(version).map { entities ->
                entities.map { it.toQuestion() }
            }
        }
    }

    private suspend fun getQuestionsByVersions(versions: List<String>): List<Question> {
        Log.d("QuestionRepository", "getQuestionsByVersions called with: $versions")
        return if (versions.isEmpty()) {
            Log.d("QuestionRepository", "No versions provided, returning empty list")
            emptyList()
        } else {
            Log.d("QuestionRepository", "Querying database for versions: $versions")
            val questions = questionDao.getQuestionsByVersions(versions).map { it.toQuestion() }
            Log.d("QuestionRepository", "Found ${questions.size} questions for versions: $versions")
            questions
        }
    }
    
    /**
     * Get a random subset of questions filtered by Java version
     */
    suspend fun getRandomQuestionsByVersion(count: Int, languageVersion: String?): List<Question> {
        val allQuestions = getQuestionsByVersion(languageVersion).first()
        return allQuestions.shuffled().take(count)
    }
    
    /**
     * Get questions filtered by category as a Flow
     */
    fun getQuestionsByCategory(category: String?): Flow<List<Question>> {
        return if (category == null || category == "All") {
            getAllQuestions()
        } else {
            questionDao.getQuestionsByCategory(category).map { entities ->
                entities.map { it.toQuestion() }
            }
        }
    }
    
    /**
     * Get questions filtered by both version and category as a Flow
     */
    fun getQuestionsByVersionAndCategory(version: String?, category: String?): Flow<List<Question>> {
        return when {
            (version == null || version == "All") && (category == null || category == "All") -> getAllQuestions()
            version == null || version == "All" -> getQuestionsByCategory(category)
            category == null || category == "All" -> getQuestionsByVersion(version)
            else -> questionDao.getQuestionsByVersionAndCategory(version, category).map { entities ->
                entities.map { it.toQuestion() }
            }
        }
    }

    /**
     * Get questions filtered by both versions and category
     */
    suspend fun getQuestionsByVersionsAndCategory(versions: List<String>?, category: String?): List<Question> {
        Log.d("QuestionRepository", "getQuestionsByVersionsAndCategory called with:")
        Log.d("QuestionRepository", "- versions: ${versions?.joinToString() ?: "null"} (${versions?.size ?: 0} items)")
        Log.d("QuestionRepository", "- category: ${category ?: "null"}")
        
        return try {
            val result = when {
                versions == null || versions.isEmpty() -> {
                    Log.d("QuestionRepository", "No versions provided, falling back to all questions")
                    // If no versions are selected, get all questions (filtered by category if specified)
                    if (category.isNullOrEmpty()) {
                        Log.d("QuestionRepository", "No category provided, getting all questions")
                        getAllQuestions().first()
                    } else {
                        Log.d("QuestionRepository", "Filtering by category: $category")
                        getQuestionsByCategory(category).first()
                    }
                }
                category.isNullOrEmpty() -> {
                    // If no category is specified, get questions for the specified versions
                    getQuestionsByVersions(versions)
                }
                else -> {
                    // If versions are provided, filter by them (and by category if specified)
                    Log.d("QuestionRepository", "Filtering by versions: $versions")
                    val questionsByVersions = getQuestionsByVersions(versions)
                    Log.d("QuestionRepository", "Found ${questionsByVersions.size} questions for versions: $versions")
                    
                    if (!category.isNullOrEmpty()) {
                        Log.d("QuestionRepository", "Further filtering by category: $category")
                        val filtered = questionsByVersions.filter { it.category == category }
                        Log.d("QuestionRepository", "After category filter: ${filtered.size} questions remain")
                        filtered
                    } else {
                        questionsByVersions
                    }
                }
            }.let { questions ->
                questions.ifEmpty {
                    Log.w(
                        "QuestionRepository",
                        "No questions found for versions: $versions, category: $category"
                    )
                    emptyList()
                }
            }
            
            Log.d("QuestionRepository", "Returning ${result.size} questions total")
            result
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error getting questions by versions and category: ${e.message}", e)
            Log.e("QuestionRepository", "Versions: $versions, Category: $category")
            emptyList()
        }
    }
    
    /**
     * Get a random subset of questions filtered by version and category
     */
    suspend fun getRandomQuestionsByVersionAndCategory(count: Int, languageVersion: String?, category: String?): List<Question> {
        val allQuestions = getQuestionsByVersionAndCategory(languageVersion, category).first()
        return allQuestions.shuffled().take(count)
    }
    
    /**
     * Get available Java version categories
     */
    suspend fun getAvailableVersions(): List<String> {
        return questionDao.getAvailableVersions()
    }
    
    /**
     * Get available categories
     */
    suspend fun getAvailableCategories(): List<String> {
        return questionDao.getAvailableCategories()
    }
    
    /**
     * Get available categories for a specific Java version
     */
    suspend fun getCategoriesByVersion(version: String): List<String> {
        return questionDao.getCategoriesByVersion(version)
    }
    
    /**
     * Initialize database with questions from QuestionBank if empty
     */
    suspend fun initializeDatabase(questions: List<Question>) {
        if (questionDao.getQuestionCount() == 0) {
            val entities = questions.map { QuestionEntity.fromQuestion(it) }
            questionDao.insertQuestions(entities)
        }
    }
    
    /**
     * Get question count
     */
    suspend fun getQuestionCount(): Int {
        return questionDao.getQuestionCount()
    }
}



