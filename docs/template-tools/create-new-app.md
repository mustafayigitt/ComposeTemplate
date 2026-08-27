# Create New App

`create-new-app` makes ComposeTemplate a real project generator instead of only a starter repository.

## Problem

Android project identity is scattered across many files:

- package declarations,
- namespace,
- application id,
- source directory paths,
- manifests,
- XML resources,
- Gradle scripts,
- documentation,
- native/JNI-related configuration.

Manual clone-and-rename instructions are easy to get wrong. A generator should make this deterministic.

## Command

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

The argument format is:

```text
<applicationId>,<AppName>
```

If arguments are not provided, the task can prompt interactively.

## Validation

The generator validates:

- application id format,
- app name format,
- target directory does not already exist.

Application ids should be lowercase dot-separated package names.

App names should start with a letter and use alphanumeric characters.

## What it does

The generator:

1. creates a sibling project directory,
2. copies template files,
3. excludes local-only files and build artifacts,
4. rewrites package names,
5. rewrites app name references,
6. moves source directories to match the new package,
7. removes `CreateNewAppPlugin.kt`,
8. removes the generator plugin registration from the generated project.

## Exclusion policy

The task intentionally excludes local or generated state such as:

- `.git`,
- `.gradle`,
- `.idea`,
- `.kotlin`,
- `.cxx`,
- `.externalNativeBuild`,
- `local.properties`,
- `secrets.properties`,
- build folders,
- APK/AAB artifacts,
- keystores,
- logs.

This keeps generated projects clean and prevents local secrets from being copied.

## Native rebrand safety

Package renaming can break convention-based JNI method names. ComposeTemplate avoids this with native registration patterns that do not rely on generated Java-style JNI names.

This is important because generated apps should be able to change package identity without breaking native secret access.

## CI validation

The CI template smoke job runs `create-new-app` and verifies that generated apps do not include local secrets, local properties, or Git metadata.

## Checklist

- [ ] generated app has the requested package name.
- [ ] generated app has the requested app name.
- [ ] source directories match the new package.
- [ ] generator code is removed from the generated project.
- [ ] local secrets are excluded.
- [ ] generated project builds after configuration.
- [ ] CI validates generator behavior.

## Repository references

- `build-logic/convention/CreateNewAppPlugin.kt`
- `.github/workflows/ci.yml`
- `secrets.properties.example`
