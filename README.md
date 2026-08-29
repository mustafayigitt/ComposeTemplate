# ComposeTemplate

A production-grade **Jetpack Compose project generator** for Android. Clone it once, run one command, and get a fully wired multi-module app: Clean Architecture feature modules, Gradle convention plugins, Hilt/KSP, Navigation3, Room, Retrofit/OkHttp, NDK-backed secret obfuscation, static analysis, baseline profiles and CI.

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

Add a fully wired feature vertical (domain / data / navigation / presentation) with:

```bash
./gradlew scaffoldFeature -PfeatureName=user_profile
```

## Documentation

All documentation lives in **[`wiki/`](wiki/README.md)** — this is the single source of truth for this project.

- [00 - Project Context](wiki/00-project-context.md)
- [01 - Module Topology and Build System](wiki/01-module-topology.md)
- [02 - Navigation and UI State](wiki/02-navigation-and-ui-state.md)
- [03 - Network and Auth Token Flow](wiki/03-network-and-auth.md)
- [04 - Secrets, Security and Hardening](wiki/04-secrets-and-hardening.md)
- [05 - Generator and Scaffolding Tooling](wiki/05-generator-and-scaffolding.md)
- [06 - Quality, Tests and CI](wiki/06-quality-tests-ci.md)
- [07 - Risks, Gaps and Open Questions](wiki/07-risks-and-gaps.md)
- [08 - Getting Started](wiki/08-getting-started.md)

New here? Read [08 - Getting Started](wiki/08-getting-started.md) for setup, required `secrets.properties` keys and the first-release checklist. Security posture, threat model and vulnerability reporting are covered in [04 - Secrets, Security and Hardening](wiki/04-secrets-and-hardening.md).

## Requirements

JDK 17, Android SDK (compileSdk 37 / targetSdk 36 / minSdk 23), NDK 27.0.12077973 with CMake. Always use the Gradle wrapper — this is a composite build.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Apache-2.0 — see [LICENSE](LICENSE).
