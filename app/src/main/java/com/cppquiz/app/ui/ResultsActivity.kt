package com.cppquiz.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.cppquiz.app.R
import com.cppquiz.app.ui.QuizActivity
import com.cppquiz.app.ui.MainActivity
/**
 * Results Activity - Shows quiz results
 * Similar to a results/summary page in web development
 */
class ResultsActivity : AppCompatActivity() {
    
    private lateinit var scoreText: TextView
    private lateinit var percentageText: TextView
    private lateinit var restartButton: MaterialButton
    private lateinit var homeButton: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        
        // Get score from previous activity (similar to route params or state in web)
        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 0)
        val category = intent.getStringExtra("category")
        val versions = intent.getStringArrayListExtra("versions")?: arrayListOf<String>().also {
    Log.e("ResultsActivity", "No versions received in intent!")
}
        
        // Debug logging
        Log.d("ResultsActivity", "Received intent extras: ${intent.extras?.keySet()?.joinToString()}")
        Log.d("ResultsActivity", "Score: $score, Total: $total, Category: $category")
        Log.d("ResultsActivity", "Versions from intent: $versions")
        
        // Initialize views
        scoreText = findViewById(R.id.scoreText)
        percentageText = findViewById(R.id.percentageText)
        restartButton = findViewById(R.id.restartButton)
        homeButton = findViewById(R.id.homeButton)
        
        // Display results
        scoreText.text = getString(R.string.score, score, total)
        
        val percentage = if (total > 0) {
            (score * 100 / total)
        } else {
            0
        }
        percentageText.text = getString(R.string.percentage, percentage)
        
        // Set up button listeners
        restartButton.setOnClickListener {
            // Start a new quiz
            Log.d("ResultsActivity", "Retry button clicked. Passing versions: $versions, category: $category")
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("category", category)
                putStringArrayListExtra("versions", ArrayList(versions ?: arrayListOf()))
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            Log.d("ResultsActivity", "Starting QuizActivity with extras: ${intent.extras?.keySet()?.joinToString()}")
            startActivity(intent)
            finish() // Close this activity
        }
        
        homeButton.setOnClickListener {
            // Go back to home
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}

