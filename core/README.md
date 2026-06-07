# Core Module

Shared utilities, base classes, and infrastructure.

## 🔐 Secret Management (NDK)

This module uses Android NDK to securely store and retrieve sensitive information like API keys and Base URLs.

### Configuration

1. Create a `secrets.properties` file in the root directory (this file is git-ignored).
2. Add your secrets:
   ```properties
   API_KEY_DEBUG="your_debug_key"
   API_KEY_RELEASE="your_release_key"
   BASE_URL_DEBUG="https://api-debug.test.com"
   BASE_URL_RELEASE="https://api.test.com"
   XOR_MASK="your_mask"
   EXPECTED_SIGNATURE_HASH="SHA256_HASH_WITHOUT_COLONS"
   ```
   *Note: To get your SHA-256 hash, run `./gradlew signingReport` and copy the SHA-256 value **without colons** (e.g., `5301...` instead of `53:01...`).*

### Initialization

Initialize the manager once in your `Application` class:

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SecretManager.initialize(this)
    }
}
```

### Usage

Retrieve secrets anywhere without needing a `Context`:

```kotlin
val baseUrl = SecretManager.getBaseUrl()
val apiKey = SecretManager.getApiKey()
```

### How it works
- **Build-time Encryption**: Secrets are read from `secrets.properties`, Hex-encrypted, and passed to the C++ compiler as definitions.
- **NDK Layer**: `native-lib.cpp` decodes the Hex strings and performs XOR decryption at runtime.
- **Signature Validation**: In non-debug builds, the native layer verifies the app's signature hash before returning any secrets, preventing unauthorized access via repackaging.

---

## 🏛️ Base Architecture

### [BaseViewModel](src/main/java/com/ytapps/composetemplate/core/base/BaseViewModel.kt)
Standardizes UI State management using `StateFlow` and one-shot UI Events (like Snackbars) using `Channel`.

### [UiText](src/main/java/com/ytapps/composetemplate/core/ui/util/UiText.kt)
A utility to handle both `String` and `@StringRes` in the ViewModel layer, ensuring the UI remains clean and localization-ready.

### [Result](src/main/java/com/ytapps/composetemplate/core/api/Result.kt)
A powerful sealed interface for API responses, standardized with `Success`, `Error`, and `Loading` states.
