# Getting Started

This page explains how to clone ComposeTemplate and generate your own Android application.

## Requirements

- Android Studio Ladybug or newer
- JDK 17+
- Android SDK
- Gradle wrapper
- CMake / NDK support
- Git

Use the repository Gradle wrapper instead of a locally installed Gradle binary.

## Clone

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
```

## Generate a new app

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

The command creates a sibling project at `../MyNewApp`.

## Configure secrets

Create `secrets.properties` in the generated project root:

```properties
API_KEY_DEBUG="your_debug_key"
API_KEY_RELEASE="your_release_key"
BASE_URL_DEBUG="https://api-debug.test.com/"
BASE_URL_RELEASE="https://api.test.com/"
STORE_FILE="release.keystore"
KEY_ALIAS="your_key_alias"
KEY_PASSWORD="your_key_password"
STORE_PASSWORD="your_store_password"
XOR_MASK="your_custom_mask_with_24_plus_chars"
EXPECTED_SIGNATURE_HASH="your_release_sha256_hex_with_or_without_colons"
NATIVE_RUNTIME_CHECKS_ENABLED=true
CERTIFICATE_PINNING_ENABLED=false
CERTIFICATE_PINS=""
```

## Validate and build

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Scaffold a feature

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```
