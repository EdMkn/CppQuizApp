package com.cppquiz.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.cppquiz.app.util.PreferencesManager
import com.cppquiz.app.R
import com.cppquiz.app.ui.SettingsActivity
import com.cppquiz.app.ui.CategorySelectionActivity
// Welcome screen - first thing users see
class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)
        applyTheme() // Set dark/light mode before showing UI

        setContentView(R.layout.activity_main)
        
        val startButton = findViewById<MaterialButton>(R.id.startButton)
        startButton.setOnClickListener {
            val intent = Intent(this, CategorySelectionActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<MaterialButton>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            openSettings()
        }
    }

    override fun onResume() {
        super.onResume()
        // Theme might have changed in settings, reapply it
        applyTheme()
    }
    
    // Switches between light and dark mode based on user preference
    private fun applyTheme() {
        if (preferencesManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}

