# Getting Started

This page is the short onboarding path for ComposeTemplate.

If you want the detailed workflow, use [Generate a New App](guides/generate-new-app.md).

## Requirements

- Android Studio Ladybug or newer
- JDK 17+
- Android SDK
- Git
- CMake / NDK support if native secret obfuscation is enabled

Use the repository Gradle wrapper instead of a locally installed Gradle binary.

## 1. Clone the template

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
```

## 2. Generate your app

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
cd ../MyNewApp
```

The generated project is created as a sibling directory.

## 3. Configure secrets

Copy the example file and replace placeholder values:

```bash
cp secrets.properties.example secrets.properties
```

Then review [Secret Management](security/secret-management.md) and [Validate Secrets](template-tools/validate-secrets.md).

## 4. Validate the project

Run the common local verification set:

```bash
./gradlew validateSecrets
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## 5. Optional: scaffold a feature

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

For details, read [Scaffold a Feature](guides/scaffold-feature.md).

## Next reading

1. [Project Philosophy](project-philosophy.md)
2. [Generate a New App](guides/generate-new-app.md)
3. [Architecture Overview](architecture/overview.md)
4. [Convention Plugins](build-system/gradle-convention-plugins.md)
5. [Release Readiness](guides/release-readiness.md)
