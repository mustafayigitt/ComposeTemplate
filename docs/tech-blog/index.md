# Tech Blog Series

The ComposeTemplate Tech Blog Series is a collection of long-form Android engineering articles.

These articles are not release notes and not short feature descriptions. Each article should teach the underlying engineering problem, explain common approaches, describe ComposeTemplate's implementation, and give readers a checklist they can apply to their own projects.

## Editorial standard

Every article should answer:

1. What real Android problem does this solve?
2. Why does it matter in production projects?
3. What are the common approaches and trade-offs?
4. How does ComposeTemplate implement it?
5. Which repository files demonstrate the idea?
6. What should a reader check in their own project?

## Recommended publishing order

1. [Building a Production-Grade Jetpack Compose Template Generator](architecture/template-generator.md)
2. [Feature Modularization with Clean Architecture in Android](architecture/feature-modularization-clean-architecture.md)
3. [Gradle Convention Plugins for Scalable Android Projects](build-system/gradle-convention-plugins-scalable-android.md)
4. [Navigation3 with Feature-Owned Screen Registration](architecture/navigation3-feature-owned-screen-registration.md)
5. [Native Secret Obfuscation with NDK, CMake and RegisterNatives](security/native-secret-obfuscation-ndk-cmake-registernatives.md)
6. [Baseline Profiles for Faster Android Startup](performance/baseline-profiles-faster-android-startup.md)
7. [Macrobenchmarking Android Apps the Right Way](performance/macrobenchmarking-android-apps.md)
8. [CI for Android Template Repositories](template-engineering/ci-android-template-repositories.md)
9. [Secret Validation and APK/AAB Secret Scanning](security/secret-validation-apk-aab-secret-scanning.md)
10. [Docs-as-Code with MkDocs and GitHub Pages](quality-dx/docs-as-code-mkdocs-github-pages.md)

## Architecture

- [Building a Production-Grade Jetpack Compose Template Generator](architecture/template-generator.md)
- [Feature Modularization with Clean Architecture in Android](architecture/feature-modularization-clean-architecture.md)
- [Navigation3 with Feature-Owned Screen Registration](architecture/navigation3-feature-owned-screen-registration.md)
- [UI State and One-Shot Events in Jetpack Compose](architecture/ui-state-one-shot-events.md)

## Build System

- [Gradle Convention Plugins for Scalable Android Projects](build-system/gradle-convention-plugins-scalable-android.md)
- [Version Catalog and Dependency Governance in Android](build-system/version-catalog-dependency-governance.md)
- [KSP, Hilt and Build Logic Integration](build-system/ksp-hilt-build-logic.md)

## Template Engineering

- [Building an Android Template Generator with Gradle](template-engineering/android-template-generator-gradle.md)
- [Feature Scaffolding for Clean Architecture](template-engineering/feature-scaffolding-clean-architecture.md)
- [CI for Android Template Repositories](template-engineering/ci-android-template-repositories.md)

## Security

- [Native Secret Obfuscation with NDK, CMake and RegisterNatives](security/native-secret-obfuscation-ndk-cmake-registernatives.md)
- [Runtime Integrity Signals on Android](security/runtime-integrity-signals.md)
- [Certificate Pinning: Security vs Operational Risk](security/certificate-pinning-security-operational-risk.md)
- [Secret Validation and APK/AAB Secret Scanning](security/secret-validation-apk-aab-secret-scanning.md)

## Performance

- [Baseline Profiles for Faster Android Startup](performance/baseline-profiles-faster-android-startup.md)
- [Macrobenchmarking Android Apps the Right Way](performance/macrobenchmarking-android-apps.md)
- [Measuring Generated Templates, Not Just Apps](performance/measuring-generated-templates.md)

## Quality and Developer Experience

- [Static Analysis with Ktlint and Detekt](quality-dx/static-analysis-ktlint-detekt.md)
- [Testing Stack: JUnit, MockK, Truth and Coroutine Tests](quality-dx/testing-stack-junit-mockk-truth-coroutines.md)
- [Docs-as-Code with MkDocs and GitHub Pages](quality-dx/docs-as-code-mkdocs-github-pages.md)
- [Retrofit, OkHttp and Network Layer Design](quality-dx/retrofit-okhttp-network-layer.md)
