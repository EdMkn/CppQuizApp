package com.cppquiz.app.data

/**
 * Data class representing a quiz question
 * Similar to a model/entity in web development
 */
data class Question(
    val id: Int,
    val questionText: String,
    val options: List<String>, // List of answer options
    val correctAnswerIndex: Int, // Index of the correct answer (0-based)
    val explanation: String, // Explanation shown after answering
    val languageVersion: String, // Language version this question relates to (17, 18, 19, 20, 21)
    val category: String = "General" // Category of the question (e.g., "Language Features", "Concurrency", "Collections", etc.)
)

