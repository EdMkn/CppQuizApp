package com.cppquiz.app

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.cppquiz.app.data.Question
import com.cppquiz.app.data.QuestionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

/**
 * Application class for initializing database and repository
 */
class QuizApplication : Application() {
    lateinit var questionRepository: QuestionRepository
    
    override fun onCreate() {
        super.onCreate()
        questionRepository = QuestionRepository(this)
        
        // Initialize database with questions from JSON file on first launch
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val questions = loadQuestionsFromJson()
                questionRepository.initializeDatabase(questions)
                Log.d("QuizApplication", "Loaded ${questions.size} questions from JSON")
            } catch (e: Exception) {
                Log.e("QuizApplication", "Error loading questions from JSON", e)
                // Fallback: try to load from QuestionBank if JSON fails
                try {
                    val fallbackQuestions = com.cppquiz.app.data.QuestionBank.getAllQuestions()
                    questionRepository.initializeDatabase(fallbackQuestions)
                    Log.d("QuizApplication", "Loaded ${fallbackQuestions.size} questions from QuestionBank (fallback)")
                } catch (fallbackError: Exception) {
                    Log.e("QuizApplication", "Error loading questions from QuestionBank fallback", fallbackError)
                }
            }
        }
    }
    
    /**
     * Load questions from JSON file in assets folder
     */
    private fun loadQuestionsFromJson(): List<Question> {
        return assets.open("questions.json").use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { reader ->
                val gson = Gson()
                val listType = object : TypeToken<List<Question>>() {}.type
                gson.fromJson(reader, listType)
            }
        }
    }
}



