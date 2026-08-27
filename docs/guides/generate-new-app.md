# Generate a New App

This guide explains how to generate a new Android project from ComposeTemplate.

## What this guide covers

You will run the `create-new-app` Gradle task, generate a sibling project, configure secrets, and run the minimum validation commands.

## Prerequisites

- Android Studio Ladybug or newer
- JDK 17+
- Android SDK
- Git
- NDK and CMake support if native secret obfuscation is enabled

Use the repository Gradle wrapper instead of a locally installed Gradle binary.

## Generate the project

From the ComposeTemplate root:

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

The argument format is:

```text
<applicationId>,<AppName>
```

Example:

```bash
./gradlew create-new-app -Pargs='com.acme.wallet,AcmeWallet' -q --console=plain
```

The generated project is created as a sibling directory:

```text
../AcmeWallet
```

## What the generator changes

The generator performs more than a simple copy:

- copies the template into a sibling directory,
- rewrites the application package,
- rewrites the app name,
- updates text-based project files,
- moves source directories to match the new package,
- removes the generator plugin from the generated app,
- excludes local-only and build-output files.

## Excluded files

The generator intentionally does not copy local state such as:

- `.git`,
- `.gradle`,
- `.idea`,
- `.kotlin`,
- `local.properties`,
- `secrets.properties`,
- build outputs,
- APK/AAB artifacts,
- keystores,
- logs.

This prevents local machine state and secrets from leaking into generated projects.

## Configure secrets

Enter the generated project:

```bash
cd ../MyNewApp
```

Create `secrets.properties` in the project root. Use `secrets.properties.example` as the source of truth for supported keys.

At minimum, configure debug/release API keys, base URLs, XOR mask, and expected signature hash.

Then validate:

```bash
./gradlew validateSecrets
```

## Validate the generated project

Run the local verification set:

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug :app:assembleRelease
```

## Troubleshooting

### Target directory already exists

The generator refuses to overwrite an existing sibling directory. Delete or rename the existing directory, then run the task again.

### Invalid application id

Use a lowercase dot-separated package name such as:

```text
com.example.myapp
```

### Invalid app name

Use an alphanumeric app name that starts with a letter, such as:

```text
MyNewApp
```

### Missing secrets

Run:

```bash
cp secrets.properties.example secrets.properties
```

Then replace placeholder values before building.

## Repository references

- `build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/CreateNewAppPlugin.kt`
- `secrets.properties.example`
- `.github/workflows/ci.yml`
