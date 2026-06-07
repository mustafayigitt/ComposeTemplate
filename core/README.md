# Core Module

Shared utilities, base classes, and infrastructure.

## 🔐 Secret Management (NDK)

This module uses Android NDK to securely store and retrieve API keys.

### Usage

1. Create a `secrets.properties` file in the root directory (this file is git-ignored).
2. Add your keys:
   ```properties
   API_KEY_DEBUG="your_debug_key"
   API_KEY_RELEASE="your_release_key"
   XOR_MASK="your_mask"
   EXPECTED_SIGNATURE_HASH="SHA256_HASH"
   ```
3. Retrieve the key in Kotlin:
   ```kotlin
   val apiKey = SecretManager.getApiKey(context)
   ```

### 💡 Finding your Signature Hash
To get your SHA-256 hash for `secrets.properties`:
```bash
./gradlew signingReport
```
Look for `SHA-256` in the output for your `release` or `debug` variant.

### 🛡️ ProGuard / R8
The native bridge and secret management are protected via `proguard-rules.pro`. These rules ensure that native methods and SecretManager members are not removed or renamed.

### How it works
- Secrets are read from `secrets.properties` during build time.
- They are passed as XOR-encrypted compiler definitions to the C++ compiler.
- JNI functions in `native-lib.cpp` verify the app's signature hash before decrypting and returning the key.
- This provides elite-level security against both static analysis and repackaging.

## 🏛️ Base Architecture

### [BaseViewModel](src/main/java/com/ytapps/composetemplate/core/base/BaseViewModel.kt)
Standardizes UI State management using `StateFlow` and one-shot UI Events (like Snackbars) using `Channel`.

### [UiText](src/main/java/com/ytapps/composetemplate/core/ui/util/UiText.kt)
A utility to handle both `String` and `@StringRes` in the ViewModel layer, ensuring the UI remains clean and localization-ready.

### [Result](src/main/java/com/ytapps/composetemplate/core/api/Result.kt)
A powerful sealed interface for API responses, standardized with `Success`, `Error`, and `Loading` states.
