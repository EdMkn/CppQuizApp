# Java Quiz App - Android Application

A modern Android quiz application focused on Java 17-21 features. Perfect for learning Android development if you're coming from web development!

## 🎯 Features

- **100+ Questions**: Comprehensive questions about Java 17, 18, 19, 20, and 21 features
- **Intuitive UI**: Clean, Material Design interface
- **Interactive Quiz**: Multiple choice questions with instant feedback
- **Score Tracking**: See your results at the end of each quiz
- **Explanations**: Learn from detailed explanations after each question

## 📱 Project Structure

For web developers, here's how Android projects map to web concepts:

```
app/
├── src/main/
│   ├── java/com/javaguiz/app/     # Your "backend" logic (like controllers/services)
│   │   ├── MainActivity.kt        # Welcome screen (like a landing page)
│   │   ├── QuizActivity.kt       # Quiz screen (like a component/page)
│   │   ├── ResultsActivity.kt    # Results screen
│   │   ├── Question.kt           # Data model (like a TypeScript interface)
│   │   └── QuestionBank.kt       # Data repository (like a database/API)
│   │
│   ├── res/                       # Resources (like static assets)
│   │   ├── layout/                # XML layouts (like HTML templates)
│   │   ├── values/                # Strings, colors, themes (like CSS/config)
│   │   └── mipmap/                # App icons
│   │
│   └── AndroidManifest.xml        # App configuration (like package.json)
│
├── build.gradle.kts               # Dependencies (like package.json)
└── settings.gradle.kts            # Project settings
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (latest version recommended)
- **JDK 17** or higher
- **Android SDK** (API 24+)

### Setup Steps

1. **Open the Project**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to this directory

2. **Sync Gradle**
   - Android Studio will automatically prompt you to sync
   - Click "Sync Now" or go to `File > Sync Project with Gradle Files`
   - This downloads dependencies (like `npm install` in web dev)

3. **Run the App**
   - Connect an Android device or start an emulator
   - Click the green "Run" button (▶️) or press `Shift + F10`
   - The app will install and launch on your device/emulator

## 📚 Key Concepts for Web Developers

### Activities = Pages/Components
- `MainActivity`: Welcome screen
- `QuizActivity`: Quiz interface
- `ResultsActivity`: Results display

### Layouts = HTML Templates
- XML files in `res/layout/` define UI structure
- Similar to JSX/HTML but in XML format
- Use `findViewById()` to access elements (like `document.getElementById()`)

### Intents = Navigation
- Used to navigate between activities
- Similar to routing in web frameworks
- Can pass data (like query params or state)

### Resources = Static Assets
- `strings.xml`: All text strings (like i18n files)
- `colors.xml`: Color definitions (like CSS variables)
- `themes.xml`: App styling (like CSS themes)

## 🎨 Customization

### Adding More Questions

Edit `QuestionBank.kt` and add more `Question` objects to the list:

```kotlin
Question(
    id = 16,
    questionText = "Your question here?",
    options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
    correctAnswerIndex = 0, // 0-based index
    explanation = "Why this answer is correct",
    languageVersion = "21"
)
```

### Changing Quiz Length

In `QuizActivity.kt`, modify this line:

```kotlin
questions = QuestionBank.getRandomQuestions(10) // Change 10 to any number
```

### Styling

- **Colors**: Edit `res/values/colors.xml`
- **Themes**: Edit `res/values/themes.xml`
- **Layouts**: Edit XML files in `res/layout/`

## 🏗️ Architecture

The app follows a simple, intuitive architecture:

1. **Data Layer**: `Question` model + `QuestionBank` repository
2. **UI Layer**: Activities + XML layouts
3. **Logic Layer**: Business logic in Activities

This is similar to MVC/MVP patterns in web development.

## 🐳 Docker & Distribution

### Building with Docker

This project includes Docker support for consistent builds:

```bash
# Build the Docker image
docker build -t javaguiz-app .

# Build the APK inside the container
docker run --rm -v $(pwd):/app -w /app javaguiz-app ./gradlew assembleDebug
```

**Why Docker?**
- ✅ Consistent build environment (same Android SDK, JDK, tools)
- ✅ Perfect for CI/CD pipelines
- ✅ No need to install Android SDK locally for builds
- ❌ Not for running the app (APKs need Android runtime)

### Automated Distribution

**GitHub Actions** workflows are set up for automated builds:

1. **Manual Build**: Go to Actions → "Build and Release APK" → Run workflow
2. **Automatic Release**: Push a version tag to create a release:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
   GitHub will automatically build and create a release with downloadable APK!

📖 **See [DOCKER.md](DOCKER.md) for detailed Docker and distribution guide.**

## 📖 Learning Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin for Android](https://developer.android.com/kotlin)
- [Material Design Components](https://material.io/components)

## 🐛 Troubleshooting

**Gradle Sync Issues?**
- Check your internet connection
- Go to `File > Invalidate Caches / Restart`
- Make sure you have JDK 17+ installed

**App Won't Run?**
- Check that you have an emulator running or device connected
- Verify `minSdk` version matches your device/emulator
- Check Logcat for error messages

## 📝 Next Steps

### ✅ Completed Features
- [x] User preferences (dark mode, question count)
- [x] Categories by Java version
- [x] Database for questions (Room)
- [x] Multi-version selection for quizzes

### 🚧 Upcoming Features
- [ ] Progress saving
- [ ] Timer for questions
- [ ] Statistics tracking
- [ ] Network support for downloading questions
- [ ] Question difficulty levels
- [ ] Review incorrect answers

## 🤝 Contributing

Feel free to add more questions or improve the UI! This is a learning project.

---

**Happy Learning! 🎓**

