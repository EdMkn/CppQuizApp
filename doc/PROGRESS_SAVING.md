# Progress Saving Implementation Guide

This guide explains how to implement progress saving for the quiz app, allowing users to resume their quiz if they leave the app.

## 🎯 Overview

Progress saving allows users to:
- Leave the quiz mid-way and resume later
- Continue from where they left off if the app is closed
- Maintain their current score and question position

## 📋 Implementation Options

### Option 1: Simple SharedPreferences (Recommended for MVP)

**Pros:**
- ✅ Simple to implement
- ✅ Fast and lightweight
- ✅ No database changes needed
- ✅ Perfect for single active quiz

**Cons:**
- ❌ Only one saved quiz at a time
- ❌ No quiz history

**Best for:** Quick implementation, single quiz session

---

### Option 2: Room Database (Advanced)

**Pros:**
- ✅ Multiple quiz sessions
- ✅ Quiz history tracking
- ✅ Statistics and analytics
- ✅ More robust data management

**Cons:**
- ❌ More complex implementation
- ❌ Requires database schema changes
- ❌ More code to maintain

**Best for:** Full-featured app with history and statistics

---

## 🚀 Option 1: SharedPreferences Implementation

### Step 1: Create Progress Data Class

Create a new file: `app/src/main/java/com/javaguiz/app/data/QuizProgress.kt`

```kotlin
package com.javaguiz.app.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Represents a saved quiz progress state
 */
data class QuizProgress(
    val questionIds: List<Int>,        // IDs of questions in this quiz
    val currentQuestionIndex: Int,     // Current position (0-based)
    val score: Int,                     // Current score
    val selectedAnswers: List<Int?>,    // Answers selected so far (null = not answered)
    val languageVersion: String?,           // Selected Java version filter (null = all)
    val timestamp: Long                 // When quiz was started
) {
    companion object {
        private val gson = Gson()
        
        /**
         * Convert to JSON string for storage
         */
        fun toJson(progress: QuizProgress): String {
            return gson.toJson(progress)
        }
        
        /**
         * Parse from JSON string
         */
        fun fromJson(json: String): QuizProgress? {
            return try {
                gson.fromJson(json, QuizProgress::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
```

### Step 2: Extend PreferencesManager

Add these methods to `PreferencesManager.kt`:

```kotlin
companion object {
    // ... existing keys ...
    const val KEY_QUIZ_PROGRESS = "quiz_progress"
}

/**
 * Save current quiz progress
 */
fun saveQuizProgress(progress: QuizProgress) {
    val json = QuizProgress.toJson(progress)
    prefs.edit().putString(KEY_QUIZ_PROGRESS, json).apply()
}

/**
 * Load saved quiz progress
 */
fun loadQuizProgress(): QuizProgress? {
    val json = prefs.getString(KEY_QUIZ_PROGRESS, null) ?: return null
    return QuizProgress.fromJson(json)
}

/**
 * Clear saved quiz progress (when quiz is completed)
 */
fun clearQuizProgress() {
    prefs.edit().remove(KEY_QUIZ_PROGRESS).apply()
}

/**
 * Check if there's a saved quiz progress
 */
fun hasQuizProgress(): Boolean {
    return prefs.contains(KEY_QUIZ_PROGRESS)
}
```

### Step 3: Update QuizActivity

Modify `QuizActivity.kt` to save and restore progress:

**Add to class properties:**
```kotlin
private var selectedAnswers: MutableList<Int?> = mutableListOf() // Track all answers
```

**Update `onCreate()` to restore progress:**
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_quiz)
    
    preferencesManager = PreferencesManager(this)
    questionRepository = (application as QuizApplication).questionRepository
    
    // Check for saved progress
    val savedProgress = preferencesManager.loadQuizProgress()
    
    if (savedProgress != null) {
        // Restore from saved progress
        restoreQuiz(savedProgress)
    } else {
        // Start new quiz
        startNewQuiz()
    }
    
    // ... rest of initialization ...
}

private fun startNewQuiz() {
    val selectedlanguageVersion = intent.getStringExtra("languageVersion")
    val questionCount = preferencesManager.getQuestionCount()
    
    lifecycleScope.launch {
        try {
            questions = if (questionCount >= 999) {
                questionRepository.getQuestionsByVersion(selectedlanguageVersion).first().shuffled()
            } else {
                questionRepository.getRandomQuestionsByVersion(questionCount, selectedlanguageVersion)
            }
            
            if (questions.isNotEmpty()) {
                // Initialize selectedAnswers list
                selectedAnswers = MutableList(questions.size) { null }
                currentQuestionIndex = 0
                score = 0
                displayQuestion()
                setupOptionButtons()
            } else {
                finish()
            }
        } catch (e: Exception) {
            Log.e("QuizActivity", "Error loading questions", e)
            finish()
        }
    }
}

private fun restoreQuiz(progress: QuizProgress) {
    lifecycleScope.launch {
        try {
            // Load questions by IDs
            questions = progress.questionIds.mapNotNull { id ->
                questionRepository.getQuestionById(id)?.toQuestion()
            }
            
            if (questions.isEmpty()) {
                // Questions not found, start new quiz
                preferencesManager.clearQuizProgress()
                startNewQuiz()
                return@launch
            }
            
            // Restore state
            currentQuestionIndex = progress.currentQuestionIndex
            score = progress.score
            selectedAnswers = progress.selectedAnswers.toMutableList()
            
            // Restore UI state for current question
            displayQuestion()
            setupOptionButtons()
            
            // If current question was already answered, show feedback
            selectedAnswers[currentQuestionIndex]?.let { answerIndex ->
                answerSubmitted = true
                val question = questions[currentQuestionIndex]
                val isCorrect = answerIndex == question.correctAnswerIndex
                showFeedback(isCorrect, question)
                highlightSelectedButton(answerIndex)
                if (!isCorrect) {
                    highlightCorrectAnswer(question.correctAnswerIndex)
                }
                disableOptionButtons()
            }
        } catch (e: Exception) {
            Log.e("QuizActivity", "Error restoring quiz", e)
            preferencesManager.clearQuizProgress()
            startNewQuiz()
        }
    }
}
```

**Update `onOptionSelected()` to track answers:**
```kotlin
private fun onOptionSelected(selectedIndex: Int) {
    if (answerSubmitted) return
    
    selectedAnswerIndex = selectedIndex
    selectedAnswers[currentQuestionIndex] = selectedIndex // Save answer
    
    val question = questions[currentQuestionIndex]
    disableOptionButtons()
    highlightSelectedButton(selectedIndex)
    
    val isCorrect = selectedIndex == question.correctAnswerIndex
    if (isCorrect) {
        score++
    }
    
    provideFeedback(isCorrect)
    showFeedback(isCorrect, question)
    answerSubmitted = true
    
    // Save progress after each answer
    saveProgress()
}
```

**Update `nextButton` click handler:**
```kotlin
nextButton.setOnClickListener {
    if (currentQuestionIndex < questions.size - 1) {
        currentQuestionIndex++
        answerSubmitted = false
        selectedAnswerIndex = null
        displayQuestion()
        resetOptionButtons()
        
        // Restore answer if already answered
        selectedAnswers[currentQuestionIndex]?.let { answerIndex ->
            answerSubmitted = true
            val question = questions[currentQuestionIndex]
            val isCorrect = answerIndex == question.correctAnswerIndex
            showFeedback(isCorrect, question)
            highlightSelectedButton(answerIndex)
            if (!isCorrect) {
                highlightCorrectAnswer(question.correctAnswerIndex)
            }
            disableOptionButtons()
        }
        
        saveProgress() // Save when moving to next question
    } else {
        // Quiz complete - clear progress
        preferencesManager.clearQuizProgress()
        goToResults()
    }
}
```

**Add save/restore lifecycle methods:**
```kotlin
override fun onPause() {
    super.onPause()
    saveProgress() // Save when app goes to background
}

override fun onDestroy() {
    super.onDestroy()
    // Don't clear progress here - user might return
    // Only clear when quiz is completed
    mediaActionSound?.release()
    mediaActionSound = null
}

private fun saveProgress() {
    if (::questions.isInitialized && questions.isNotEmpty()) {
        val progress = QuizProgress(
            questionIds = questions.map { it.id },
            currentQuestionIndex = currentQuestionIndex,
            score = score,
            selectedAnswers = selectedAnswers.toList(),
            languageVersion = intent.getStringExtra("languageVersion"),
            timestamp = System.currentTimeMillis()
        )
        preferencesManager.saveQuizProgress(progress)
    }
}
```

### Step 4: Add Resume Option in MainActivity

Update `MainActivity.kt` to show a "Resume Quiz" button if progress exists:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    val preferencesManager = PreferencesManager(this)
    
    // Check for saved progress
    if (preferencesManager.hasQuizProgress()) {
        // Show resume button
        val resumeButton = findViewById<MaterialButton>(R.id.resumeButton)
        resumeButton?.visibility = View.VISIBLE
        resumeButton?.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
    
    // ... rest of initialization ...
}
```

**Update `activity_main.xml` to include resume button:**
```xml
<com.google.android.material.button.MaterialButton
    android:id="@+id/resumeButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Resume Quiz"
    android:visibility="gone"
    ... />
```

---

## 🏗️ Option 2: Room Database Implementation

For a more robust solution with quiz history, create these additional files:

### Step 1: Create QuizSession Entity

`app/src/main/java/com/javaguiz/app/data/QuizSession.kt`

```kotlin
package com.javaguiz.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "quiz_sessions")
@TypeConverters(Converters::class)
data class QuizSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionIds: List<Int>,
    val selectedAnswers: List<Int?>,
    val score: Int,
    val totalQuestions: Int,
    val languageVersion: String?,
    val startTime: Long,
    val endTime: Long? = null,
    val isCompleted: Boolean = false,
    val currentQuestionIndex: Int = 0
)
```

### Step 2: Create QuizSessionDao

Add to `QuestionDao.kt` or create separate file:

```kotlin
@Dao
interface QuizSessionDao {
    @Query("SELECT * FROM quiz_sessions WHERE isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveSession(): QuizSession?
    
    @Query("SELECT * FROM quiz_sessions WHERE isCompleted = 1 ORDER BY endTime DESC")
    fun getCompletedSessions(): Flow<List<QuizSession>>
    
    @Insert
    suspend fun insertSession(session: QuizSession): Long
    
    @Update
    suspend fun updateSession(session: QuizSession)
    
    @Query("DELETE FROM quiz_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)
}
```

### Step 3: Update Database

Update `QuizDatabase.kt`:

```kotlin
@Database(
    entities = [QuestionEntity::class, QuizSession::class],
    version = 2, // Increment version
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun quizSessionDao(): QuizSessionDao
}
```

---

## 🎨 UI Considerations

### Resume Dialog

Show a dialog when user starts a new quiz but has existing progress:

```kotlin
private fun showResumeDialog() {
    AlertDialog.Builder(this)
        .setTitle("Resume Quiz?")
        .setMessage("You have an unfinished quiz. Would you like to resume it?")
        .setPositiveButton("Resume") { _, _ ->
            // Restore quiz
        }
        .setNegativeButton("Start New") { _, _ ->
            preferencesManager.clearQuizProgress()
            startNewQuiz()
        }
        .setNeutralButton("Cancel", null)
        .show()
}
```

### Progress Indicator

Add a progress bar showing quiz completion:

```xml
<ProgressBar
    android:id="@+id/quizProgressBar"
    style="@android:style/Widget.ProgressBar.Horizontal"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:max="100"
    android:progress="0" />
```

Update in `displayQuestion()`:
```kotlin
val progress = ((currentQuestionIndex + 1) * 100) / questions.size
quizProgressBar.progress = progress
```

---

## ✅ Testing Checklist

- [ ] Save progress when leaving quiz
- [ ] Restore progress when returning
- [ ] Clear progress when quiz completes
- [ ] Handle edge cases (questions deleted, app updated)
- [ ] Test with app killed and restarted
- [ ] Test with multiple quiz sessions
- [ ] Verify score accuracy after restore

---

## 🔧 Troubleshooting

**Progress not saving?**
- Check SharedPreferences keys are correct
- Verify Gson serialization works
- Check logs for errors

**Progress not restoring?**
- Verify questions still exist in database
- Check question IDs match
- Handle null cases gracefully

**Score incorrect after restore?**
- Ensure score is recalculated or stored correctly
- Verify answer tracking is accurate

---

## 📝 Next Steps

After implementing basic progress saving, consider:
- [ ] Auto-save every N questions
- [ ] Quiz expiration (clear old progress)
- [ ] Multiple quiz sessions
- [ ] Statistics dashboard
- [ ] Export/import progress




