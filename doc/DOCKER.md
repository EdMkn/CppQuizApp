# Docker & Distribution Guide

## 🤔 Is Docker Useful for Android Apps?

### ✅ **YES, for CI/CD and Build Environments**

Docker is **very useful** for:
- **Consistent Build Environment**: Ensures everyone builds with the same Android SDK, JDK, and tools
- **CI/CD Pipelines**: Automated builds in GitHub Actions, GitLab CI, Jenkins, etc.
- **Reproducible Builds**: Same environment = same build results
- **Isolation**: No need to install Android SDK on your machine for CI

### ❌ **NO, for Running the App**

Docker is **NOT useful** for:
- Running the Android app itself (APKs need Android runtime/emulator)
- Distribution to end users (they download APK files, not containers)

## 📦 What Gets Distributed?

**APK files** are what users download and install on their Android devices:
- `app-debug.apk` - Debug build (for testing)
- `app-release.apk` - Release build (for distribution)

## 🚀 How to Use Docker for Building

### Option 1: Build with Caching (Recommended)

**Use the helper script** (handles caching automatically):
```bash
./docker-build.sh
```

**Or manually with cache:**
```bash
# Enable BuildKit for advanced caching
export DOCKER_BUILDKIT=1

# Build the Docker image (with layer caching)
docker build -t cppquiz-app .

# Build APK with Gradle cache mounted (faster subsequent builds)
docker run --rm \
  -v $(pwd):/app \
  -v gradle-cache:/root/.gradle/caches \
  -v gradle-wrapper:/root/.gradle/wrapper \
  cppquiz-app ./gradlew assembleDebug --no-daemon

# The APK will be in: app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Build Without Cache (Slower)

```bash
# Build the Docker image
docker build -t cppquiz-app .

# Build the APK inside the container
docker run --rm -v $(pwd):/app -w /app cppquiz-app ./gradlew assembleDebug

# The APK will be in: app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Use Pre-built Image (if pushed to registry)

```bash
# Pull and run
docker pull your-registry/cppquiz-app:latest
docker run --rm -v $(pwd):/app -w /app your-registry/cppquiz-app ./gradlew assembleDebug
```

## 🔄 Automated Distribution Workflows

### GitHub Actions (Recommended)

Two workflows are set up:

1. **`.github/workflows/build-and-release.yml`**
   - Builds APK on push or manual trigger
   - Creates GitHub Release when you push a version tag (e.g., `v1.0.0`)
   - Uploads APK as downloadable artifact

2. **`.github/workflows/docker-build.yml`**
   - Builds using Docker container
   - Demonstrates Docker-based CI/CD

### How to Create a Release

1. **Tag a version:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. **GitHub Actions will automatically:**
   - Build the APK
   - Create a GitHub Release
   - Attach the APK for download

3. **Users can download:**
   - Go to your GitHub repo → Releases
   - Download the APK file
   - Install on Android device

## 📱 Distribution Options

### 1. GitHub Releases (Free, Easy)
- ✅ Automatic via GitHub Actions
- ✅ Public or private
- ✅ Version history
- ✅ Release notes

### 2. Google Play Store (Production)
- Requires Google Play Developer account ($25 one-time)
- App signing required
- Review process
- Best for public distribution

### 3. Firebase App Distribution (Free)
- Beta testing
- Internal testing
- Easy distribution to testers

### 4. Direct APK Distribution
- Host APK on your website
- Share via email/cloud storage
- Users enable "Install from unknown sources"

## 🔐 Signing for Release Builds

For production releases, you need to sign your APK:

1. **Generate a keystore:**
   ```bash
   keytool -genkey -v -keystore my-release-key.jks \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias my-key-alias
   ```

2. **Add to `app/build.gradle.kts`:**
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("my-release-key.jks")
               storePassword = System.getenv("KEYSTORE_PASSWORD")
               keyAlias = "my-key-alias"
               keyPassword = System.getenv("KEY_PASSWORD")
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               isMinifyEnabled = true
           }
       }
   }
   ```

3. **Store secrets in GitHub Secrets** (for CI/CD)

## 🛠️ Docker Commands Reference

```bash
# Enable BuildKit for caching
export DOCKER_BUILDKIT=1

# Build image (with layer caching)
docker build -t cppquiz-app .

# Build APK (debug) with Gradle cache
docker run --rm \
  -v $(pwd):/app \
  -v gradle-cache:/root/.gradle/caches \
  -v gradle-wrapper:/root/.gradle/wrapper \
  cppquiz-app ./gradlew assembleDebug --no-daemon

# Build APK (release) with Gradle cache
docker run --rm \
  -v $(pwd):/app \
  -v gradle-cache:/root/.gradle/caches \
  -v gradle-wrapper:/root/.gradle/wrapper \
  cppquiz-app ./gradlew assembleRelease --no-daemon

# Run tests with cache
docker run --rm \
  -v $(pwd):/app \
  -v gradle-cache:/root/.gradle/caches \
  cppquiz-app ./gradlew test --no-daemon

# Clean build
docker run --rm -v $(pwd):/app cppquiz-app ./gradlew clean
```

## 💾 Caching Strategy

The Dockerfile uses **multi-layer caching** for optimal performance:

1. **Base Layers** (cached - rarely change):
   - JDK installation
   - System dependencies
   - Android SDK installation

2. **Dependency Layers** (cached - changes when dependencies change):
   - Gradle wrapper
   - Build configuration files (`build.gradle.kts`)
   - Gradle dependencies (downloaded once, cached)

3. **Source Code Layer** (rebuilds on every code change):
   - Application source code

**Cache Benefits:**
- ✅ First build: ~5-10 minutes (downloads everything)
- ✅ Subsequent builds: ~1-2 minutes (uses cached layers)
- ✅ Only rebuilds when dependencies or source code change

**Gradle Cache Volumes:**
- `gradle-cache`: Stores downloaded dependencies (persists across builds)
- `gradle-wrapper`: Stores Gradle wrapper files (persists across builds)

## 📋 Best Practices

1. **Use Docker for CI/CD**: Ensures consistent builds across environments
2. **Sign Release Builds**: Required for production distribution
3. **Version Your Tags**: Use semantic versioning (v1.0.0, v1.1.0, etc.)
4. **Store Secrets Securely**: Use GitHub Secrets, not hardcoded values
5. **Test Before Release**: Always test debug builds before creating releases

## 🎯 Quick Start

1. **For local builds:**
   ```bash
   ./gradlew assembleDebug
   # APK: app/build/outputs/apk/debug/app-debug.apk
   ```

2. **For Docker builds:**
   ```bash
   docker build -t cppquiz-app .
   docker run --rm -v $(pwd):/app -w /app cppquiz-app ./gradlew assembleDebug
   ```

3. **For automated releases:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   # GitHub Actions will handle the rest!
   ```

---

**Summary**: Docker is great for building, but APK files are what users install! 🚀


