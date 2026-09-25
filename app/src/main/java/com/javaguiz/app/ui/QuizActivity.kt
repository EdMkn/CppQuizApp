package com.javaguiz.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaActionSound
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.javaguiz.app.util.PreferencesManager
import com.javaguiz.app.data.Question
import com.javaguiz.app.data.QuestionRepository
import com.javaguiz.app.ui.ResultsActivity
import com.javaguiz.app.QuizApplication
import com.javaguiz.app.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
// Main quiz screen - handles question display and answer checking
class QuizActivity : AppCompatActivity() {
    
    // UI references - need these to update the screen
    private lateinit var questionCounter: TextView
    private lateinit var languageVersionBadge: TextView
    private lateinit var questionText: TextView
    private lateinit var optionButton1: MaterialButton
    private lateinit var optionButton2: MaterialButton
    private lateinit var optionButton3: MaterialButton
    private lateinit var optionButton4: MaterialButton
    private lateinit var feedbackText: TextView
    private lateinit var explanationText: TextView
    private lateinit var nextButton: MaterialButton
    
    // Quiz state tracking
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var score = 0
    private var versions: List<String> = emptyList() 
    private var category: String = ""
    private var selectedAnswerIndex: Int? = null
    private var answerSubmitted = false // Prevent changing answer after submission

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var questionRepository: QuestionRepository
    private var mediaActionSound: MediaActionSound? = null // Nullable because sound might be disabled
    private val attributionContext: Context by lazy {
        // Create attribution context once for this activity (Android 11+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            createAttributionContext("quiz_feedback")
        } else {
            this
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        
        // Log all received intent extras
        Log.d("QuizActivity", "Intent extras: ${intent?.extras?.keySet()?.joinToString()}")
        
        versions = intent.getStringArrayListExtra("versions") ?: emptyList<String>()
        category = intent.getStringExtra("category") ?: "no category"
        Log.d("QuizActivity", "Parsed versions: $versions (size: ${versions.size}), category: $category")
        
        if (versions.isEmpty()) {
            Log.w("QuizActivity", "No versions received in intent! This might cause issues.")
        }
        
        // Get repository from Application
        questionRepository = (application as QuizApplication).questionRepository
        preferencesManager = PreferencesManager(this)

        initializeViews()

        // Set up sound feedback - only if enabled (saves resources)
        // Preloading here so sounds play instantly when needed
        try {
            if (preferencesManager.isSoundEnabled()) {
                mediaActionSound = MediaActionSound()
                mediaActionSound?.load(MediaActionSound.SHUTTER_CLICK) // For correct answers
                mediaActionSound?.load(MediaActionSound.FOCUS_COMPLETE) // For wrong answers
                Log.d("QuizActivity", "MediaActionSound initialized and sounds preloaded")
            } else {
                Log.d("QuizActivity", "Sound is disabled, skipping MediaActionSound initialization")
            }
        } catch (e: Exception) {
            Log.e("QuizActivity", "Failed to initialize MediaActionSound", e)
            mediaActionSound = null
        }

        // Load questions
        lifecycleScope.launch {
            try {
                val questionCount = preferencesManager.getQuestionCount()
                Log.d("QuizActivity", "Loading $questionCount questions for versions: $versions (size: ${versions.size})")
                
                // Log the actual values being passed to the repository
                Log.d("QuizActivity", "Calling getQuestionsByVersionsAndCategory with:")
                Log.d("QuizActivity", "- versions: $versions (${versions.javaClass.simpleName})")
                Log.d("QuizActivity", "- category: $category (${category?.javaClass?.simpleName})")
                
                questions = if (questionCount >= 999) {
                    // Get all questions for selected versions and category
                    val questions = questionRepository.getQuestionsByVersionsAndCategory(
                        versions,
                        category.ifEmpty { null }
                    )
                    Log.d("QuizActivity", "Loaded ${questions.size} questions for versions: $versions")
                    if (questions.isEmpty()) {
                        Log.e("QuizActivity", "WARNING: No questions found for versions: $versions, category: $category")
                    }
                    questions
                } else {
                    // Get random subset of questions
                    val allQuestions = questionRepository.getQuestionsByVersionsAndCategory(
                        versions,
                        category?.ifEmpty { null }
                    )
                    allQuestions.shuffled().take(questionCount)
                }
                
                // Only proceed if we have questions
                if (questions.isNotEmpty()) {
                    displayQuestion()
                    setupOptionButtons()
                } else {
                    // Handle empty questions case
                    Log.e("QuizActivity", "No questions found for versions: $versions")
                    showErrorAndFinish("No questions available for the selected criteria")
                }
            } catch (e: Exception) {
                Log.e("QuizActivity", "Error loading questions", e)
                showErrorAndFinish("Error loading questions. Please try again.")
            }
        }
        
        // Handle "Next" button - either go to next question or finish quiz
        nextButton.setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                answerSubmitted = false
                selectedAnswerIndex = null
                displayQuestion()
                resetOptionButtons()
            } else {
                goToResults(score, questions.size)
            }
        }
    }
    
    /**
     * Initialize all view references
     * Similar to querySelector in web development
     */
    private fun initializeViews() {
        questionCounter = findViewById(R.id.questionCounter)
        languageVersionBadge = findViewById(R.id.languageVersionBadge)
        questionText = findViewById(R.id.questionText)
        optionButton1 = findViewById(R.id.optionButton1)
        optionButton2 = findViewById(R.id.optionButton2)
        optionButton3 = findViewById(R.id.optionButton3)
        optionButton4 = findViewById(R.id.optionButton4)
        feedbackText = findViewById(R.id.feedbackText)
        explanationText = findViewById(R.id.explanationText)
        nextButton = findViewById(R.id.nextButton)
    }
    
    // Updates the screen with the current question
    private fun displayQuestion() {
        try {
            if (questions.isEmpty()) {
                Log.e("QuizActivity", "No questions available to display")
                showErrorAndFinish("No questions available. Please try another category.")
                return
            }
            
            if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size) {
                Log.e("QuizActivity", "Invalid question index: $currentQuestionIndex, total questions: ${questions.size}")
                showErrorAndFinish("Invalid question. Please restart the quiz.")
                return
            }
            
            val question = questions[currentQuestionIndex]
            
            // Update question counter and version badge
            questionCounter.text = getString(R.string.question_counter, currentQuestionIndex + 1, questions.size)
            languageVersionBadge.text = getString(R.string.cpp_version, question.languageVersion)
            questionText.text = question.questionText ?: "[No question text]"
            
            // Validate and set answer options
            val options = question.options
            if (options.size < 4) {
                Log.w("QuizActivity", "Question has less than 4 options: ${options.size}")
                // Handle case where we have fewer than 4 options
                optionButton1.text = options.getOrNull(0) ?: "Option 1"
                optionButton2.text = options.getOrNull(1) ?: "Option 2"
                optionButton3.text = options.getOrNull(2) ?: "Option 3"
                optionButton4.text = options.getOrNull(3) ?: "Option 4"
                
                // Disable buttons that don't have valid options
                optionButton1.isEnabled = options.size > 0
                optionButton2.isEnabled = options.size > 1
                optionButton3.isEnabled = options.size > 2
                optionButton4.isEnabled = false // Always disable the 4th button if we don't have 4 options
            } else {
                // We have at least 4 options
                optionButton1.text = options[0]
                optionButton2.text = options[1]
                optionButton3.text = options[2]
                optionButton4.text = options[3]
                
                // Make sure all buttons are enabled
                enableOptionButtons()
            }
            
            // Hide feedback elements initially
            feedbackText.visibility = View.GONE
            explanationText.visibility = View.GONE
            nextButton.visibility = View.GONE
            
            // Reset button styles
            resetOptionButtons()
            
        } catch (e: Exception) {
            Log.e("QuizActivity", "Error displaying question", e)
            showErrorAndFinish("Error loading question. Please try again.")
        }
    }
    
    private fun showErrorAndFinish(message: String) {
        // Show error message
        feedbackText.visibility = View.VISIBLE
        feedbackText.text = message
        feedbackText.setTextColor(Color.RED)
        
        // Disable all option buttons
        disableOptionButtons()
        
        // Show next button to allow user to proceed
        nextButton.visibility = View.VISIBLE
        nextButton.text = getString(android.R.string.ok)
        nextButton.setOnClickListener {
            finish()
        }
    }
    
    // Wire up the answer buttons
    private fun setupOptionButtons() {
        optionButton1.setOnClickListener { onOptionSelected(0) }
        optionButton2.setOnClickListener { onOptionSelected(1) }
        optionButton3.setOnClickListener { onOptionSelected(2) }
        optionButton4.setOnClickListener { onOptionSelected(3) }
    }
    
    // Called when user taps an answer
    private fun onOptionSelected(selectedIndex: Int) {
        try {
            Log.d("QuizActivity", "onOptionSelected called with index: $selectedIndex")
            
            // Check if we can proceed with answer selection
            if (answerSubmitted) {
                Log.d("QuizActivity", "Answer already submitted, ignoring selection")
                return // Can't change answer after submitting
            }
            
            if (questions.isEmpty() || currentQuestionIndex >= questions.size) {
                Log.e("QuizActivity", "No questions available or invalid question index")
                return
            }
            
            val question = questions.getOrNull(currentQuestionIndex) ?: run {
                Log.e("QuizActivity", "Question at index $currentQuestionIndex is null")
                return
            }
            
            if (selectedIndex < 0 || selectedIndex >= question.options.size) {
                Log.e("QuizActivity", "Invalid selected index: $selectedIndex, options size: ${question.options.size}")
                return
            }
            
            selectedAnswerIndex = selectedIndex
            Log.d("QuizActivity", "Selected answer index: $selectedIndex, Correct answer index: ${question.correctAnswerIndex}")
            
            try {
                disableOptionButtons() // Lock in the answer
                highlightSelectedButton(selectedIndex)
                
                val isCorrect = selectedIndex == question.correctAnswerIndex
                Log.d("QuizActivity", "Answer is correct: $isCorrect")
                
                if (isCorrect) {
                    score++
                    Log.d("QuizActivity", "Score updated to: $score")
                }

                // Play sound/vibration if enabled
                Log.d("QuizActivity", "Calling provideFeedback with isCorrect=$isCorrect")
                provideFeedback(isCorrect)
                
                showFeedback(isCorrect, question)
                answerSubmitted = true
            } catch (e: Exception) {
                Log.e("QuizActivity", "Error processing answer selection", e)
                // Show error to user
                feedbackText.visibility = View.VISIBLE
                feedbackText.text = "An error occurred. Please try again."
                feedbackText.setTextColor(Color.RED)
                enableOptionButtons() // Re-enable buttons to allow retry
            }
        } catch (e: Exception) {
            Log.e("QuizActivity", "Unexpected error in onOptionSelected", e)
            // Show error to user
            feedbackText.visibility = View.VISIBLE
            feedbackText.text = "An unexpected error occurred. Please restart the quiz."
            feedbackText.setTextColor(Color.RED)
            nextButton.visibility = View.VISIBLE // Allow user to proceed
        }
    }

    // Plays sound and/or vibration based on user preferences
    private fun provideFeedback(isCorrect: Boolean) {
        Log.d("QuizActivity", "=== provideFeedback called ===")
        Log.d("QuizActivity", "isCorrect: $isCorrect")
        
        // Check sound preference
        val soundEnabled = preferencesManager.isSoundEnabled()
        Log.d("QuizActivity", "Sound enabled in preferences: $soundEnabled")
        Log.d("QuizActivity", "Sound preference key: ${PreferencesManager.KEY_SOUND_ENABLED}")
        
        // Check vibration preference
        val vibrationEnabled = preferencesManager.isVibrationEnabled()
        Log.d("QuizActivity", "Vibration enabled in preferences: $vibrationEnabled")
        
        // Vibration feedback
        if (vibrationEnabled) {
            Log.d("QuizActivity", "Attempting to provide vibration feedback...")
            try {
                val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    // For Android 12 (API 31) and above
                    val vibratorManager = attributionContext.getSystemService(VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
                    vibratorManager?.defaultVibrator
                } else {
                    // For older versions
                    @Suppress("DEPRECATION")
                    attributionContext.getSystemService(VIBRATOR_SERVICE) as? android.os.Vibrator
                }

                if (vibrator?.hasVibrator() == true) {
                    Log.d("QuizActivity", "Vibrator is available, triggering vibration...")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            android.os.VibrationEffect.createOneShot(
                                if (isCorrect) 50 else 100,
                                android.os.VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                        Log.d("QuizActivity", "Vibration triggered (API 26+): ${if (isCorrect) 50 else 100}ms")
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(if (isCorrect) 50 else 100)
                        Log.d("QuizActivity", "Vibration triggered (API <26): ${if (isCorrect) 50 else 100}ms")
                    }
                } else {
                    Log.w("QuizActivity", "Vibrator is not available or hasVibrator() returned false")
                }
            } catch (e: Exception) {
                Log.e("QuizActivity", "Error providing vibration feedback", e)
            }
        } else {
            Log.d("QuizActivity", "Vibration is disabled in preferences, skipping...")
        }
        
        // Play sound if enabled - different sounds for right/wrong
        if (soundEnabled) {
            Log.d("QuizActivity", "Sound is enabled, attempting to play sound...")
            try {
                if (mediaActionSound != null) {
                    val soundType = if (isCorrect) {
                        MediaActionSound.SHUTTER_CLICK // Happy sound
                    } else {
                        MediaActionSound.FOCUS_COMPLETE // Different tone for wrong
                    }
                    
                    // Don't call load() - already preloaded in onCreate()
                    mediaActionSound?.play(soundType)
                    Log.d("QuizActivity", "Sound played successfully: ${if (isCorrect) "correct (SHUTTER_CLICK)" else "incorrect (FOCUS_COMPLETE)"}")
                } else {
                    Log.w("QuizActivity", "MediaActionSound is null, cannot play sound. Sound may have been disabled or initialization failed.")
                    // Fallback: try to init on-the-fly (shouldn't happen normally)
                    try {
                        mediaActionSound = MediaActionSound()
                        val soundType = if (isCorrect) MediaActionSound.SHUTTER_CLICK else MediaActionSound.FOCUS_COMPLETE
                        mediaActionSound?.load(soundType)
                        mediaActionSound?.play(soundType)
                        Log.d("QuizActivity", "MediaActionSound initialized on-the-fly and sound played")
                    } catch (e: Exception) {
                        Log.e("QuizActivity", "Failed to initialize MediaActionSound on-the-fly", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("QuizActivity", "Error playing sound feedback", e)
            }
        } else {
            Log.d("QuizActivity", "Sound is disabled in preferences, skipping sound feedback...")
        }
        
        Log.d("QuizActivity", "=== provideFeedback completed ===")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up sound resources
        try {
            mediaActionSound?.release()
            mediaActionSound = null
            Log.d("QuizActivity", "MediaActionSound released")
        } catch (e: Exception) {
            Log.e("QuizActivity", "Error releasing MediaActionSound", e)
        }
    }

    // Shows whether answer was right/wrong and the explanation
    private fun showFeedback(isCorrect: Boolean, question: Question) {
        feedbackText.visibility = View.VISIBLE
        explanationText.visibility = View.VISIBLE
        nextButton.visibility = View.VISIBLE
        
        if (isCorrect) {
            feedbackText.text = getString(R.string.correct_answer)
            feedbackText.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            feedbackText.text = getString(R.string.incorrect_answer)
            feedbackText.setTextColor(Color.parseColor("#F44336"))
            
            // Highlight the correct answer
            highlightCorrectAnswer(question.correctAnswerIndex)
        }
        
        // Show explanation
        explanationText.text = "${getString(R.string.explanation)}\n${question.explanation}"
    }
    
    // Highlights the button user just tapped (uses theme color so it works in dark mode)
    private fun highlightSelectedButton(index: Int) {
        val button = getOptionButton(index)
        // Get primary color from theme (adapts to light/dark mode)
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        val primaryColor = typedValue.data
        // Semi-transparent background
        val selectedBg = Color.argb(30, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor))
        button.setBackgroundColor(selectedBg)
        button.setTextColor(primaryColor)
    }
    
    // Shows the correct answer in green when user gets it wrong
    private fun highlightCorrectAnswer(index: Int) {
        val button = getOptionButton(index)
        button.setBackgroundColor(Color.parseColor("#C8E6C9")) // Light green
        button.setTextColor(Color.parseColor("#2E7D32")) // Dark green
    }
    
    // Helper to get button by index (0-3)
    private fun getOptionButton(index: Int): MaterialButton {
        return when (index) {
            0 -> optionButton1
            1 -> optionButton2
            2 -> optionButton3
            3 -> optionButton4
            else -> optionButton1
        }
    }
    
    // Re-enable buttons for new question
    private fun enableOptionButtons() {
        optionButton1.isEnabled = true
        optionButton2.isEnabled = true
        optionButton3.isEnabled = true
        optionButton4.isEnabled = true
    }
    
    // Lock buttons after answer is selected
    private fun disableOptionButtons() {
        optionButton1.isEnabled = false
        optionButton2.isEnabled = false
        optionButton3.isEnabled = false
        optionButton4.isEnabled = false
    }
    
    // Reset button colors to default (important for dark mode)
    private fun resetOptionButtons() {
        val buttons = listOf(optionButton1, optionButton2, optionButton3, optionButton4)
        // Use Material colorOnSurface for proper dark mode support
        // This ensures text is visible on both light and dark backgrounds
        val typedValue = TypedValue()
        val attributeResId = com.google.android.material.R.attr.colorOnSurface
        if (theme.resolveAttribute(attributeResId, typedValue, true)) {
            val textColor = typedValue.data
            buttons.forEach { button ->
                button.setBackgroundColor(Color.TRANSPARENT)
                button.setTextColor(textColor)
                button.isEnabled = true
            }
        } else {
            // Fallback to textColorPrimary if colorOnSurface is not available
            val fallbackTypedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.textColorPrimary, fallbackTypedValue, true)
            val textColor = fallbackTypedValue.data
            buttons.forEach { button ->
                button.setBackgroundColor(Color.TRANSPARENT)
                button.setTextColor(textColor)
                button.isEnabled = true
            }
        }
    }
    
    // Quiz finished - show results and close this screen
    private fun goToResults(score: Int, total: Int) {
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("score", score)
            putExtra("total", total)
            putExtra("category", intent.getStringExtra("category"))
            putExtra("versions", ArrayList(versions))
        }
        startActivity(intent)
        finish()
    }
}

