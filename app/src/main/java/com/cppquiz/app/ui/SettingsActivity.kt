package com.cppquiz.app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.cppquiz.app.util.PreferencesManager
import com.cppquiz.app.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize default value in SharedPreferences BEFORE loading the fragment
        // This ensures the ListPreference has a value when it tries to render
        // Use commit() instead of apply() to ensure synchronous write
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!defaultPrefs.contains(PreferencesManager.KEY_QUESTION_COUNT)) {
            defaultPrefs.edit()
                .putString(PreferencesManager.KEY_QUESTION_COUNT, "10")
                .commit()
        } else {
            // Ensure the value exists and is valid
            val currentValue = defaultPrefs.getString(PreferencesManager.KEY_QUESTION_COUNT, null)
            if (currentValue == null || currentValue.isEmpty()) {
                defaultPrefs.edit()
                    .putString(PreferencesManager.KEY_QUESTION_COUNT, "10")
                    .commit()
            }
        }

        setContentView(R.layout.activity_settings)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!supportFragmentManager.popBackStackImmediate()) {
            finish()
        }
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // Set the value BEFORE loading preferences to prevent null pointer
            // Use commit() to ensure synchronous write before preferences load
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            if (!prefs.contains(PreferencesManager.KEY_QUESTION_COUNT)) {
                prefs.edit().putString(PreferencesManager.KEY_QUESTION_COUNT, "10").commit()
            }
            
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Get the ListPreference and ensure it has a valid value
            val listPreference = findPreference<ListPreference>(PreferencesManager.KEY_QUESTION_COUNT)
            listPreference?.let { pref ->
                // CRITICAL: Set entryValues programmatically to ensure they're not null
                // The arrays might not be loaded properly from XML, so set them explicitly
                val entries = resources.getStringArray(R.array.question_count_entries)
                val entryValues = resources.getStringArray(R.array.question_count_values)
                
                Log.d("SettingsActivity", "Setting entries: ${entries.contentToString()}")
                Log.d("SettingsActivity", "Setting entryValues: ${entryValues.contentToString()}")
                
                pref.entries = entries
                pref.entryValues = entryValues
                
                // Ensure value is set and valid BEFORE setting summary provider
                // Set it directly in SharedPreferences first (reuse prefs from outer scope)
                val storedValue = prefs.getString(PreferencesManager.KEY_QUESTION_COUNT, null)
                if (storedValue == null || storedValue.isEmpty()) {
                    prefs.edit().putString(PreferencesManager.KEY_QUESTION_COUNT, "10").commit()
                    pref.value = "10"
                } else {
                    pref.value = storedValue
                }
                
                // Remove click listener - let default behavior handle it
                // We'll ensure value is set in onDisplayPreferenceDialog instead
                
                // Set a custom summary provider to handle null values safely
                // This will be called when the preference needs to display its summary
                pref.summaryProvider = androidx.preference.Preference.SummaryProvider<ListPreference> { preference ->
                    try {
                        val value = preference.value
                        if (value != null && value.isNotEmpty()) {
                            val index = preference.findIndexOfValue(value)
                            if (index >= 0 && index < preference.entries.size) {
                                val entry = preference.entries[index]
                                entry?.toString() ?: getString(R.string.pref_question_count_summary)
                            } else {
                                getString(R.string.pref_question_count_summary)
                            }
                        } else {
                            getString(R.string.pref_question_count_summary)
                        }
                    } catch (e: Exception) {
                        getString(R.string.pref_question_count_summary)
                    }
                }
            }

            //Listen for preference changes
            findPreference<androidx.preference.SwitchPreferenceCompat>(PreferencesManager.KEY_DARK_MODE)?.setOnPreferenceChangeListener { _, _ ->
                activity?.recreate()
                true
            }
        }
        
        override fun onStart() {
            super.onStart()
            // Ensure value is set when fragment becomes visible
            val listPreference = findPreference<ListPreference>(PreferencesManager.KEY_QUESTION_COUNT)
            listPreference?.let { pref ->
                val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val currentValue = prefs.getString(PreferencesManager.KEY_QUESTION_COUNT, null)
                if (currentValue == null || currentValue.isEmpty()) {
                    prefs.edit().putString(PreferencesManager.KEY_QUESTION_COUNT, "10").commit()
                    pref.value = "10"
                } else if (pref.value == null || pref.value.isEmpty()) {
                    pref.value = currentValue
                }
            }
        }
        override fun onDisplayPreferenceDialog(preference: Preference) {
            // Ensure the ListPreference has a valid value before showing the dialog
            if (preference is ListPreference && preference.key == PreferencesManager.KEY_QUESTION_COUNT) {
                // DEBUG: Log current state
                val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val storedValue = prefs.getString(PreferencesManager.KEY_QUESTION_COUNT, null)
                val prefValue = preference.value
                Log.d("SettingsActivity", "onDisplayPreferenceDialog - storedValue: $storedValue, prefValue: $prefValue")
                
                // Check entryValues for nulls
                val entryValues = preference.entryValues
                Log.d("SettingsActivity", "entryValues size: ${entryValues?.size}")
                entryValues?.forEachIndexed { index, value ->
                    if (value == null) {
                        Log.e("SettingsActivity", "NULL entryValue at index $index!")
                    } else {
                        Log.d("SettingsActivity", "entryValues[$index] = ${value.toString()}")
                    }
                }
                
                // Set value in SharedPreferences FIRST (synchronously)
                var currentValue = storedValue
                if (currentValue == null || currentValue.isEmpty()) {
                    currentValue = "10"
                    prefs.edit().putString(PreferencesManager.KEY_QUESTION_COUNT, currentValue).commit()
                    Log.d("SettingsActivity", "Set default value to: $currentValue")
                }
                
                // CRITICAL: Set the value on the preference object
                preference.value = currentValue
                Log.d("SettingsActivity", "Set preference.value to: $currentValue, actual value now: ${preference.value}")
                
                // Verify the value is set before opening dialog
                val verifyValue = preference.value
                if (verifyValue == null || verifyValue.isEmpty()) {
                    Log.e("SettingsActivity", "ERROR: preference.value is still null after setting!")
                    preference.value = "10"
                }
                
                // Force a small delay to ensure the value is fully synced before dialog opens
                Handler(Looper.getMainLooper()).postDelayed({
                    val finalValue = preference.value
                    Log.d("SettingsActivity", "About to open dialog, final preference.value: $finalValue")
                    super.onDisplayPreferenceDialog(preference)
                }, 50) // 50ms delay should be enough
                return // Don't call super immediately
            }
            super.onDisplayPreferenceDialog(preference)
        }
    }

}