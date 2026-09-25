# Network Support Implementation Guide

This guide explains how to add network support to download questions from a remote server, enabling dynamic question updates without app updates.

## 🎯 Overview

Network support allows:
- ✅ Download questions from a remote API/server
- ✅ Update questions without app updates
- ✅ Sync questions across devices
- ✅ Add new questions remotely
- ✅ Offline-first approach (cached questions work offline)

## 📋 Architecture Overview

```
┌─────────────────┐
│   Remote API    │ (JSON endpoint)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Retrofit API   │ (HTTP client)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ QuestionService │ (Repository layer)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Room Database   │ (Local cache)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   UI Layer      │ (Activities)
└─────────────────┘
```

## 🚀 Step-by-Step Implementation

### Step 1: Add Network Dependencies

Update `app/build.gradle.kts`:

```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Retrofit for HTTP requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp for HTTP client (logging, interceptors)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Already have Gson, but ensure it's there:
    // implementation("com.google.code.gson:gson:2.10.1")
}
```

### Step 2: Add Internet Permission

Update `app/src/main/AndroidManifest.xml`:

```xml
<manifest ...>
    <!-- Add internet permission -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application ...>
        ...
    </application>
</manifest>
```

### Step 3: Create API Response Models

Create `app/src/main/java/com/cppquiz/app/data/network/ApiResponse.kt`:

```kotlin
package com.cppquiz.app.data.network

import com.cppquiz.app.data.Question

/**
 * Response model from the API
 */
data class QuestionsApiResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<Question>? = null,
    val version: String? = null, // API version or question set version
    val timestamp: Long? = null
)

/**
 * Error response model
 */
data class ApiError(
    val code: Int,
    val message: String,
    val details: String? = null
)
```

### Step 4: Create Retrofit API Service

Create `app/src/main/java/com/cppquiz/app/data/network/QuestionApiService.kt`:

```kotlin
package com.cppquiz.app.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for question API endpoints
 * Similar to defining API routes in Express.js/Next.js
 */
interface QuestionApiService {
    /**
     * Get all questions from the server
     */
    @GET("questions")
    suspend fun getAllQuestions(): Response<QuestionsApiResponse>
    
    /**
     * Get questions by C++ version
     */
    @GET("questions")
    suspend fun getQuestionsByVersion(
        @Query("version") version: String
    ): Response<QuestionsApiResponse>
    
    /**
     * Get questions updated since a timestamp
     */
    @GET("questions/updates")
    suspend fun getUpdatedQuestions(
        @Query("since") timestamp: Long
    ): Response<QuestionsApiResponse>
    
    /**
     * Get question metadata (version, count, etc.)
     */
    @GET("questions/metadata")
    suspend fun getMetadata(): Response<QuestionMetadata>
}

/**
 * Metadata about available questions
 */
data class QuestionMetadata(
    val totalQuestions: Int,
    val availableVersions: List<String>,
    val lastUpdated: Long,
    val apiVersion: String
)
```

### Step 5: Create Network Module

Create `app/src/main/java/com/cppquiz/app/data/network/NetworkModule.kt`:

```kotlin
package com.cppquiz.app.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network configuration and setup
 * Similar to axios configuration in web development
 */
object NetworkModule {
    // TODO: Replace with your actual API base URL
    private const val BASE_URL = "https://api.example.com/quiz/"
    
    // For development/testing, you can use a local server:
    // private const val BASE_URL = "http://10.0.2.2:3000/" // Android emulator localhost
    
    /**
     * Create OkHttp client with logging and timeouts
     */
    private fun createOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Create Retrofit instance
     */
    fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Create API service instance
     */
    fun createApiService(context: Context): QuestionApiService {
        return createRetrofit(context).create(QuestionApiService::class.java)
    }
    
    /**
     * Check if device has internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected == true
        }
    }
}
```

**Note:** You'll need to add `BuildConfig` import or create a simple config:

```kotlin
// Create app/src/main/java/com/cppquiz/app/BuildConfig.kt
package com.cppquiz.app

object BuildConfig {
    const val DEBUG = true // Or use actual BuildConfig.DEBUG
}
```

### Step 6: Create Network Repository

Create `app/src/main/java/com/cppquiz/app/data/network/QuestionNetworkRepository.kt`:

```kotlin
package com.cppquiz.app.data.network

import android.content.Context
import android.util.Log
import com.cppquiz.app.data.Question
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository for network operations
 * Handles API calls and error handling
 */
class QuestionNetworkRepository(context: Context) {
    private val apiService = NetworkModule.createApiService(context)
    private val appContext = context.applicationContext
    
    /**
     * Fetch all questions from the server
     */
    suspend fun fetchAllQuestions(): Result<List<Question>> {
        return try {
            if (!NetworkModule.isNetworkAvailable(appContext)) {
                return Result.failure(NetworkException("No internet connection"))
            }
            
            val response = apiService.getAllQuestions()
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Log.d("NetworkRepo", "Fetched ${apiResponse.data.size} questions")
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(NetworkException(apiResponse.message ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("NetworkRepo", "API error: $errorBody")
                Result.failure(NetworkException("API error: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Log.e("NetworkRepo", "HTTP exception", e)
            Result.failure(NetworkException("HTTP error: ${e.code()}", e))
        } catch (e: IOException) {
            Log.e("NetworkRepo", "Network exception", e)
            Result.failure(NetworkException("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Log.e("NetworkRepo", "Unexpected error", e)
            Result.failure(NetworkException("Unexpected error: ${e.message}", e))
        }
    }
    
    /**
     * Fetch questions by C++ version
     */
    suspend fun fetchQuestionsByVersion(version: String): Result<List<Question>> {
        return try {
            if (!NetworkModule.isNetworkAvailable(appContext)) {
                return Result.failure(NetworkException("No internet connection"))
            }
            
            val response = apiService.getQuestionsByVersion(version)
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(NetworkException(apiResponse.message ?: "Unknown error"))
                }
            } else {
                Result.failure(NetworkException("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(NetworkException("Error fetching questions: ${e.message}", e))
        }
    }
    
    /**
     * Fetch updated questions since a timestamp
     */
    suspend fun fetchUpdatedQuestions(since: Long): Result<List<Question>> {
        return try {
            if (!NetworkModule.isNetworkAvailable(appContext)) {
                return Result.failure(NetworkException("No internet connection"))
            }
            
            val response = apiService.getUpdatedQuestions(since)
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Result.success(apiResponse.data)
                } else {
                    Result.failure(NetworkException(apiResponse.message ?: "Unknown error"))
                }
            } else {
                Result.failure(NetworkException("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(NetworkException("Error fetching updates: ${e.message}", e))
        }
    }
    
    /**
     * Get question metadata
     */
    suspend fun getMetadata(): Result<QuestionMetadata> {
        return try {
            if (!NetworkModule.isNetworkAvailable(appContext)) {
                return Result.failure(NetworkException("No internet connection"))
            }
            
            val response = apiService.getMetadata()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(NetworkException("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(NetworkException("Error fetching metadata: ${e.message}", e))
        }
    }
}

/**
 * Custom exception for network errors
 */
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
```

### Step 7: Update QuestionRepository

Update `app/src/main/java/com/cppquiz/app/data/QuestionRepository.kt`:

```kotlin
package com.cppquiz.app.data

import android.content.Context
import android.util.Log
import com.cppquiz.app.data.network.QuestionNetworkRepository
import com.cppquiz.app.data.network.NetworkException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionRepository(context: Context) {
    private val database = QuizDatabase.getDatabase(context)
    private val questionDao = database.questionDao()
    private val networkRepository = QuestionNetworkRepository(context)
    private val appContext = context.applicationContext
    
    // ... existing methods ...
    
    /**
     * Sync questions from server and update local database
     * Returns true if sync was successful
     */
    suspend fun syncQuestionsFromServer(): Boolean {
        return try {
            val result = networkRepository.fetchAllQuestions()
            
            result.onSuccess { questions ->
                // Save to database
                val entities = questions.map { QuestionEntity.fromQuestion(it) }
                questionDao.insertQuestions(entities)
                Log.d("QuestionRepository", "Synced ${questions.size} questions from server")
                return true
            }.onFailure { error ->
                Log.e("QuestionRepository", "Failed to sync questions", error)
                return false
            }
            
            false
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error syncing questions", e)
            false
        }
    }
    
    /**
     * Sync only updated questions (incremental update)
     */
    suspend fun syncUpdatedQuestions(lastSyncTime: Long): Boolean {
        return try {
            val result = networkRepository.fetchUpdatedQuestions(lastSyncTime)
            
            result.onSuccess { questions ->
                if (questions.isNotEmpty()) {
                    val entities = questions.map { QuestionEntity.fromQuestion(it) }
                    questionDao.insertQuestions(entities) // Insert or update
                    Log.d("QuestionRepository", "Synced ${questions.size} updated questions")
                }
                return true
            }.onFailure { error ->
                if (error !is NetworkException || error.message?.contains("No internet") == false) {
                    Log.e("QuestionRepository", "Failed to sync updates", error)
                }
                return false
            }
            
            false
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error syncing updates", e)
            false
        }
    }
    
    /**
     * Initialize database: try network first, fallback to local
     */
    suspend fun initializeDatabase(questions: List<Question>) {
        // Only initialize if database is empty
        if (questionDao.getQuestionCount() == 0) {
            // Try to fetch from server first
            val networkResult = networkRepository.fetchAllQuestions()
            
            networkResult.onSuccess { networkQuestions ->
                // Use network questions
                val entities = networkQuestions.map { QuestionEntity.fromQuestion(it) }
                questionDao.insertQuestions(entities)
                Log.d("QuestionRepository", "Initialized from network: ${networkQuestions.size} questions")
            }.onFailure { error ->
                // Fallback to local questions
                Log.w("QuestionRepository", "Network failed, using local questions", error)
                val entities = questions.map { QuestionEntity.fromQuestion(it) }
                questionDao.insertQuestions(entities)
                Log.d("QuestionRepository", "Initialized from local: ${questions.size} questions")
            }
        }
    }
}
```

### Step 8: Add Sync Preferences

Update `PreferencesManager.kt`:

```kotlin
companion object {
    // ... existing keys ...
    const val KEY_LAST_SYNC_TIME = "last_sync_time"
    const val KEY_AUTO_SYNC_ENABLED = "auto_sync_enabled"
}

fun getLastSyncTime(): Long {
    return prefs.getLong(KEY_LAST_SYNC_TIME, 0)
}

fun setLastSyncTime(timestamp: Long) {
    prefs.edit().putLong(KEY_LAST_SYNC_TIME, timestamp).apply()
}

fun isAutoSyncEnabled(): Boolean {
    return prefs.getBoolean(KEY_AUTO_SYNC_ENABLED, true)
}

fun setAutoSyncEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_AUTO_SYNC_ENABLED, enabled).apply()
}
```

### Step 9: Create Sync Service/Manager

Create `app/src/main/java/com/cppquiz/app/data/SyncManager.kt`:

```kotlin
package com.cppquiz.app.data

import android.content.Context
import android.util.Log
import com.cppquiz.app.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages question synchronization with remote server
 */
class SyncManager(private val context: Context) {
    private val repository = QuestionRepository(context)
    private val preferencesManager = PreferencesManager(context)
    
    /**
     * Perform full sync (download all questions)
     */
    suspend fun performFullSync(): SyncResult {
        return try {
            val success = repository.syncQuestionsFromServer()
            
            if (success) {
                preferencesManager.setLastSyncTime(System.currentTimeMillis())
                SyncResult.Success("Questions synced successfully")
            } else {
                SyncResult.Error("Failed to sync questions")
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Sync error", e)
            SyncResult.Error("Sync error: ${e.message}")
        }
    }
    
    /**
     * Perform incremental sync (only updated questions)
     */
    suspend fun performIncrementalSync(): SyncResult {
        return try {
            val lastSyncTime = preferencesManager.getLastSyncTime()
            val success = repository.syncUpdatedQuestions(lastSyncTime)
            
            if (success) {
                preferencesManager.setLastSyncTime(System.currentTimeMillis())
                SyncResult.Success("Questions updated successfully")
            } else {
                SyncResult.Error("Failed to update questions")
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Incremental sync error", e)
            SyncResult.Error("Update error: ${e.message}")
        }
    }
    
    /**
     * Auto-sync if enabled and enough time has passed
     */
    fun autoSyncIfNeeded(scope: CoroutineScope) {
        if (!preferencesManager.isAutoSyncEnabled()) {
            return
        }
        
        val lastSync = preferencesManager.getLastSyncTime()
        val now = System.currentTimeMillis()
        val syncInterval = 24 * 60 * 60 * 1000 // 24 hours
        
        if (now - lastSync > syncInterval) {
            scope.launch(Dispatchers.IO) {
                performIncrementalSync()
            }
        }
    }
}

/**
 * Result of sync operation
 */
sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class Error(val message: String) : SyncResult()
}
```

### Step 10: Add Sync UI

Create `app/src/main/java/com/cppquiz/app/ui/SyncActivity.kt`:

```kotlin
package com.cppquiz.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cppquiz.app.R
import com.cppquiz.app.data.SyncManager
import com.cppquiz.app.data.SyncResult
import com.cppquiz.app.util.PreferencesManager
import kotlinx.coroutines.launch

class SyncActivity : AppCompatActivity() {
    private lateinit var syncManager: SyncManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var syncButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var lastSyncText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)
        
        syncManager = SyncManager(this)
        preferencesManager = PreferencesManager(this)
        
        syncButton = findViewById(R.id.syncButton)
        progressBar = findViewById(R.id.syncProgressBar)
        statusText = findViewById(R.id.syncStatusText)
        lastSyncText = findViewById(R.id.lastSyncText)
        
        updateLastSyncTime()
        
        syncButton.setOnClickListener {
            performSync()
        }
    }
    
    private fun performSync() {
        syncButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        statusText.text = "Syncing..."
        
        lifecycleScope.launch {
            val result = syncManager.performFullSync()
            
            when (result) {
                is SyncResult.Success -> {
                    statusText.text = result.message
                    updateLastSyncTime()
                }
                is SyncResult.Error -> {
                    statusText.text = result.message
                }
            }
            
            syncButton.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }
    
    private fun updateLastSyncTime() {
        val lastSync = preferencesManager.getLastSyncTime()
        if (lastSync > 0) {
            val date = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(lastSync))
            lastSyncText.text = "Last sync: $date"
        } else {
            lastSyncText.text = "Never synced"
        }
    }
}
```

Create `app/src/main/res/layout/activity_sync.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sync Questions"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="32dp" />

    <ProgressBar
        android:id="@+id/syncProgressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        android:layout_marginBottom="16dp" />

    <TextView
        android:id="@+id/syncStatusText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Ready to sync"
        android:layout_marginBottom="16dp" />

    <TextView
        android:id="@+id/lastSyncText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Never synced"
        android:textSize="14sp"
        android:layout_marginBottom="32dp" />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/syncButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Sync Now" />

</LinearLayout>
```

### Step 11: Update Settings Activity

Add sync option to `SettingsActivity.kt`:

```kotlin
// In onCreate or settings setup
val syncPreference = findPreference<Preference>("sync_now")
syncPreference?.setOnPreferenceClickListener {
    val intent = Intent(this, SyncActivity::class.java)
    startActivity(intent)
    true
}
```

Update `res/xml/preferences.xml` (or create if doesn't exist):

```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Existing preferences -->
    
    <PreferenceCategory android:title="Sync">
        <Preference
            android:key="sync_now"
            android:title="Sync Questions"
            android:summary="Download latest questions from server" />
        
        <SwitchPreferenceCompat
            android:key="auto_sync_enabled"
            android:title="Auto Sync"
            android:summary="Automatically sync questions daily"
            android:defaultValue="true" />
    </PreferenceCategory>
    
</PreferenceScreen>
```

### Step 12: Auto-Sync on App Start

Update `QuizApplication.kt`:

```kotlin
override fun onCreate() {
    super.onCreate()
    questionRepository = QuestionRepository(this)
    
    val syncManager = SyncManager(this)
    
    // Initialize database
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val questions = loadQuestionsFromJson()
            questionRepository.initializeDatabase(questions)
            Log.d("QuizApplication", "Loaded ${questions.size} questions from JSON")
        } catch (e: Exception) {
            Log.e("QuizApplication", "Error loading questions from JSON", e)
            // ... fallback ...
        }
        
        // Auto-sync if enabled
        syncManager.autoSyncIfNeeded(CoroutineScope(Dispatchers.IO))
    }
}
```

---

## 🌐 Setting Up a Test Server

### Option 1: Simple Node.js/Express Server

Create `server.js`:

```javascript
const express = require('express');
const cors = require('cors');
const fs = require('fs');
const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

// Read questions from JSON file
const questions = JSON.parse(fs.readFileSync('questions.json', 'utf8'));

// Get all questions
app.get('/questions', (req, res) => {
    const version = req.query.version;
    
    let filtered = questions;
    if (version && version !== 'All') {
        filtered = questions.filter(q => q.languageVersion === version);
    }
    
    res.json({
        success: true,
        data: filtered,
        version: '1.0',
        timestamp: Date.now()
    });
});

// Get metadata
app.get('/questions/metadata', (req, res) => {
    res.json({
        totalQuestions: questions.length,
        availableVersions: [...new Set(questions.map(q => q.languageVersion))],
        lastUpdated: Date.now(),
        apiVersion: '1.0'
    });
});

app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
```

### Option 2: Use JSON Server (Quick Testing)

```bash
npm install -g json-server
json-server --watch questions.json --port 3000
```

Then update `NetworkModule.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/" // Android emulator
// For real device: use your computer's IP address
```

---

## 🔒 Security Considerations

### 1. HTTPS Only in Production

```kotlin
// In NetworkModule.kt
private const val BASE_URL = if (BuildConfig.DEBUG) {
    "http://10.0.2.2:3000/" // HTTP for local dev
} else {
    "https://api.yourdomain.com/quiz/" // HTTPS for production
}
```

### 2. Certificate Pinning (Advanced)

Add to `build.gradle.kts`:
```kotlin
implementation("com.squareup.okhttp3:okhttp:4.12.0")
```

Create certificate pinner in `NetworkModule.kt`:
```kotlin
private fun createOkHttpClient(context: Context): OkHttpClient {
    val certificatePinner = CertificatePinner.Builder()
        .add("api.yourdomain.com", "sha256/YOUR_CERTIFICATE_HASH")
        .build()
    
    return OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        // ... rest of config
        .build()
}
```

### 3. API Key Authentication (Optional)

```kotlin
// Add interceptor
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", apiKey)
            .build()
        return chain.proceed(request)
    }
}

// Use in OkHttpClient
.addInterceptor(ApiKeyInterceptor("your-api-key"))
```

---

## 🧪 Testing

### Test Network Availability

```kotlin
// In QuizActivity or test
if (NetworkModule.isNetworkAvailable(context)) {
    // Show sync option
} else {
    // Show offline message
}
```

### Test API Endpoints

Use tools like:
- **Postman** - Test API endpoints
- **curl** - Command line testing
- **Android Studio Network Profiler** - Monitor network calls

---

## 📱 Error Handling Best Practices

### Show User-Friendly Messages

```kotlin
when (result) {
    is SyncResult.Success -> {
        Toast.makeText(this, "Questions updated!", Toast.LENGTH_SHORT).show()
    }
    is SyncResult.Error -> {
        val message = when {
            error.message?.contains("No internet") == true -> 
                "No internet connection. Please check your network."
            error.message?.contains("timeout") == true -> 
                "Request timed out. Please try again."
            else -> "Failed to sync. Please try again later."
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
```

### Retry Logic

```kotlin
suspend fun syncWithRetry(maxRetries: Int = 3): SyncResult {
    repeat(maxRetries) { attempt ->
        val result = performFullSync()
        if (result is SyncResult.Success) {
            return result
        }
        if (attempt < maxRetries - 1) {
            delay(2000 * (attempt + 1)) // Exponential backoff
        }
    }
    return SyncResult.Error("Failed after $maxRetries attempts")
}
```

---

## ✅ Checklist

- [ ] Add network dependencies (Retrofit, OkHttp)
- [ ] Add internet permission to manifest
- [ ] Create API service interface
- [ ] Create network repository
- [ ] Update QuestionRepository with sync methods
- [ ] Add sync preferences
- [ ] Create SyncManager
- [ ] Add sync UI (activity/dialog)
- [ ] Test with local server
- [ ] Test offline behavior
- [ ] Add error handling
- [ ] Set up production API endpoint
- [ ] Enable HTTPS in production
- [ ] Test on real device

---

## 🚀 Next Steps

After basic network support:
- [ ] Background sync with WorkManager
- [ ] Question versioning and conflict resolution
- [ ] Download progress indicator
- [ ] Question caching strategy
- [ ] Rate limiting
- [ ] Analytics for sync success/failure

---

## 📚 Resources

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [OkHttp Documentation](https://square.github.io/okhttp/)
- [Android Network Security Config](https://developer.android.com/training/articles/security-config)
- [Android ConnectivityManager](https://developer.android.com/reference/android/net/ConnectivityManager)




