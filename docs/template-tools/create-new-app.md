# Create New App

`create-new-app` makes ComposeTemplate a real project generator.

## Command

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

## What it does

- Copies the template to a sibling directory
- Rewrites package name
- Rewrites app name
- Moves source directories
- Removes generator-specific code from the generated app
- Excludes `.git`, `.gradle`, `.idea`, `local.properties`, `secrets.properties`, and build outputs

## Native JNI rebrand support

ComposeTemplate uses `JNI_OnLoad` + `RegisterNatives`, with target class path provided through Gradle namespace/CMake configuration, so native bindings survive package rebranding.
