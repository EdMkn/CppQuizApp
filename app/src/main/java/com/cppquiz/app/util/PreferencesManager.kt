package com.cppquiz.app.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

// Wrapper around SharedPreferences - makes it easier to read/write settings
class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        private const val PREFS_NAME = "JavaQuizzPreferences" // Not used but kept for reference

        // Keys used in SharedPreferences
        const val KEY_QUESTION_COUNT = "question_count"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        
        // Defaults if nothing is stored yet
        const val DEFAULT_QUESTION_COUNT = 10
        const val DEFAULT_DARK_MODE = false
        const val DEFAULT_SOUND_ENABLED = true
        const val DEFAULT_VIBRATION_ENABLED = true
    }

    // Note: question_count is stored as String (because ListPreference uses strings)
    // but we return Int for convenience
    fun getQuestionCount(): Int {
        val stringValue = prefs.getString(KEY_QUESTION_COUNT, null)
        if (stringValue != null) {
            return try {
                stringValue.toInt()
            } catch (e: NumberFormatException) {
                DEFAULT_QUESTION_COUNT
            }
        }
        // Fallback for old installs that might have stored it as int
        return prefs.getInt(KEY_QUESTION_COUNT, DEFAULT_QUESTION_COUNT)
    }
    
    fun setQuestionCount(count: Int) {
        prefs.edit().putInt(KEY_QUESTION_COUNT, count).apply()
    }
    
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE)
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
    
    // Added logging here to debug sound issues
    fun isSoundEnabled(): Boolean {
        val value = prefs.getBoolean(KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED)
        Log.d("PreferencesManager", "isSoundEnabled() called")
        Log.d("PreferencesManager", "  Key: $KEY_SOUND_ENABLED")
        Log.d("PreferencesManager", "  Default value: $DEFAULT_SOUND_ENABLED")
        Log.d("PreferencesManager", "  Stored value: $value")
        Log.d("PreferencesManager", "  Preference exists: ${prefs.contains(KEY_SOUND_ENABLED)}")
        return value
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }
    
    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, DEFAULT_VIBRATION_ENABLED)
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }
}