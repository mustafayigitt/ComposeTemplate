# Native Secret Obfuscation with NDK, CMake and RegisterNatives

Client-side secrets are never truly secret. Still, Android apps can increase extraction cost and prevent accidental leakage.

## Problem

Putting API keys or internal metadata directly into `BuildConfig` leaves values easy to extract from APKs. Moving them to native code does not make them impossible to recover, but it raises the reverse-engineering bar.

## ComposeTemplate approach

ComposeTemplate uses `core:secrets`, Android NDK, CMake, and native code to avoid plain Kotlin string exposure.

The native layer is configured through Gradle convention plugins and receives build-time values through CMake arguments.

## RegisterNatives

Hardcoded JNI method names are fragile because package renaming breaks them. ComposeTemplate uses `JNI_OnLoad` and `RegisterNatives`, with the target class path injected from the Gradle namespace.

## Takeaway

Native obfuscation is a cost-increasing layer, not a security boundary. Use it honestly and pair it with backend-side controls.
