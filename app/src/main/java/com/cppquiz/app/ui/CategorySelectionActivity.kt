package com.cppquiz.app.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.cppquiz.app.data.QuestionRepository
import com.cppquiz.app.QuizApplication
import com.cppquiz.app.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Category selection screen - allows users to choose a Java version and/or category
 */
class CategorySelectionActivity : AppCompatActivity() {
    
    private lateinit var questionRepository: QuestionRepository
    private lateinit var step1CategorySelection: View
    private lateinit var step2VersionSelection: View
    private lateinit var selectedCategoryText: TextView
    private lateinit var versionButtonsRecycler: RecyclerView
    private lateinit var backButton: Button
    private lateinit var startQuizButton: Button
    private var selectedVersions = mutableListOf<String>()
    private var selectedCategory: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        step1CategorySelection = findViewById(R.id.step1CategorySelection)
        step2VersionSelection = findViewById(R.id.step2VersionSelection)
        selectedCategoryText = findViewById(R.id.selectedCategoryText)
        versionButtonsRecycler = findViewById(R.id.versionButtonsRecycler)
        backButton = findViewById(R.id.backButton)
        startQuizButton = findViewById(R.id.startQuizButton)
        startQuizButton.visibility = View.GONE
        startQuizButton.setOnClickListener {
            if (selectedVersions.isNotEmpty()) {
                startQuiz(selectedVersions, selectedCategory)
            }else {
                Toast.makeText(this, "Please select at least one version", Toast.LENGTH_SHORT).show()
            }
        }
        // Set up RecyclerView for version buttons
        versionButtonsRecycler.layoutManager = LinearLayoutManager(this)
        versionButtonsRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        // Set up back button
        backButton.setOnClickListener {
            step1CategorySelection.visibility = View.VISIBLE
            step2VersionSelection.visibility = View.GONE
        }
        
        // Get repository from Application
        questionRepository = (application as QuizApplication).questionRepository
        
        // Load available versions and categories asynchronously from database
        lifecycleScope.launch {
            try {
                val availableVersions = questionRepository.getAvailableVersions()
                val availableCategories = questionRepository.getAvailableCategories()
                // Update UI on main thread
                setupVersionButtons(availableVersions)
                setupCategoryButtons(availableCategories)
            } catch (e: Exception) {
                // Handle error - show all buttons as fallback
                android.util.Log.e("CategorySelectionActivity", "Error loading data", e)
                setupVersionButtons(emptyList())
                setupCategoryButtons(emptyList())
            }
        }
        
        // All Questions button
        val allButton = findViewById<MaterialButton>(R.id.categoryAllButton)
        allButton.setOnClickListener {
            startQuiz(null, null)
        }
    }
    
    private fun setupVersionButtons(versions: List<String>) {
        val adapter = VersionAdapter(versions) { selectedVersions ->
            this.selectedVersions = selectedVersions.toMutableList()
            startQuizButton.visibility = if (selectedVersions.isNotEmpty()) View.VISIBLE else View.GONE
        }
        versionButtonsRecycler.adapter = adapter
    }

    private fun showVersionSelection(categoryName: String) {
        selectedCategoryText.text = getString(R.string.selected_category, categoryName)
        step1CategorySelection.visibility = View.GONE
        step2VersionSelection.visibility = View.VISIBLE
        
        // Load available versions
        lifecycleScope.launch {
            try {
                val versions = questionRepository.getAvailableVersions()
                setupVersionButtons(versions)
            } catch (e: Exception) {
                Log.e("CategorySelection", "Error loading versions", e)
                // Fallback to default versions if there's an error
                setupVersionButtons(listOf("23", "20", "17", "14", "11", "Core"))
            }
        }
    }
    
    private fun setupCategoryButtons(categories: List<String>) {
        val categoryButtonMap: Map<Int, String> = mapOf(
            R.id.categoryAllButton to getString(R.string.all_categories),
            R.id.categoryLanguageFeaturesButton to getString(R.string.category_language_features),
            R.id.categoryConcurrencyButton to getString(R.string.category_concurrency),
            R.id.categoryCollectionsButton to getString(R.string.category_collections),
            R.id.categoryAPIsButton to getString(R.string.category_apis),
            R.id.categoryCoreConceptsButton to getString(R.string.category_core_concepts),
            R.id.categoryAdvancedButton to getString(R.string.category_advanced),
            R.id.categoryGeneralButton to getString(R.string.category_general)
        )
        categoryButtonMap.forEach { (buttonId, category) ->
            val button = findViewById<MaterialButton>(buttonId)
            if (categories.contains(category) || category == getString(R.string.all_categories)) {
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    selectedCategory = if (category == getString(R.string.all_categories)) null else category
                    showVersionSelection(category)
                }
            } else {
                button.visibility = View.GONE
            }
        }
    }
    
    private fun startQuiz(versions: List<String>?, category: String?) {
        Log.d("QuizStart", "Starting quiz with version: $versions, category: $category")
        
        lifecycleScope.launch {
            try {
                val questions = questionRepository.getQuestionsByVersionsAndCategory(
                    versions ?: emptyList(), 
                    category?.ifEmpty { null }
                )
                
                if (questions.isEmpty()) {
                    // Show a toast or dialog to inform the user
                    android.widget.Toast.makeText(
                        this@CategorySelectionActivity,
                        "No questions available for the selected criteria",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                
                val intent = Intent(this@CategorySelectionActivity, QuizActivity::class.java).apply {
                    putStringArrayListExtra("versions", ArrayList(versions?: emptyList()))
                    putExtra("category", category ?: "")
                }
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("CategorySelection", "Error checking questions", e)
                android.widget.Toast.makeText(
                    this@CategorySelectionActivity,
                    "Error checking questions: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}

private class VersionAdapter(
        private val versions: List<String>,
        private val onVersionsSelected: (List<String>) -> Unit
    ) : RecyclerView.Adapter<VersionAdapter.VersionViewHolder>() {
        private val selectedVersions = mutableListOf<String>()
        class VersionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val button: MaterialButton = view as MaterialButton
        }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionViewHolder {
                val button = MaterialButton(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val padding = resources.getDimensionPixelSize(R.dimen.button_padding)
                    setPadding(padding, padding, padding, padding)
                    textSize = 16f
                    isCheckable = true
                    isChecked = false

                   // Apply both selectors
                    background = ContextCompat.getDrawable(context, R.drawable.version_button_background_selector)
                    setTextColor(ContextCompat.getColorStateList(context, R.color.version_button_color_selector))
                }
                return VersionViewHolder(button)
            }
            override fun onBindViewHolder(holder: VersionViewHolder, position: Int) {
                val version = versions[position]
                holder.button.text = when (version) {
                    "Core" -> holder.button.context.getString(R.string.core_cpp)
                    else -> "C++ $version"
                }
                
                // Update the button's appearance based on selection
                holder.button.isChecked = selectedVersions.contains(version)
                updateButtonAppearance(holder.button, holder.button.isChecked)

                holder.button.setOnClickListener {
                    if (holder.button.isChecked) {
                        if (!selectedVersions.contains(version)) {
                            selectedVersions.add(version)
                        }
                    } else {
                        selectedVersions.remove(version)
                    }
                    onVersionsSelected(selectedVersions)
                }
            }

            private fun updateButtonAppearance(button: MaterialButton, isSelected: Boolean) {
                val context = button.context
                if (isSelected) {
                    button.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
                    button.setTextColor(Color.WHITE)
                } else {
                    button.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
                    button.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }
            override fun getItemCount() = versions.size
    }

