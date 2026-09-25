package com.cppquiz.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * Room entity representing a quiz question in the database
 */
@Entity(tableName = "questions")
@TypeConverters(Converters::class)
data class QuestionEntity(
    @PrimaryKey
    val id: Int,
    val questionText: String,
    val options: List<String>, // Will be converted to JSON string
    val correctAnswerIndex: Int,
    val explanation: String,
    val languageVersion: String,
    val category: String = "General"
) {
    /**
     * Convert entity to domain model
     */
    fun toQuestion(): Question {
        return Question(
            id = id,
            questionText = questionText,
            options = options,
            correctAnswerIndex = correctAnswerIndex,
            explanation = explanation,
            languageVersion = languageVersion,
            category = category
        )
    }
    
    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromQuestion(question: Question): QuestionEntity {
            return QuestionEntity(
                id = question.id,
                questionText = question.questionText,
                options = question.options,
                correctAnswerIndex = question.correctAnswerIndex,
                explanation = question.explanation,
                languageVersion = question.languageVersion,
                category = question.category
            )
        }
    }
}



