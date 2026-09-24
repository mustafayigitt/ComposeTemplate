# ComposeTemplate

A production-grade **Jetpack Compose project generator** for Android. Every retained feature has the same `data`, `domain`, `navigation`, and `presentation` modules, while project-level data infrastructure is selected at generation time.

```bash
git clone https://github.com/mustafayigitt/ComposeTemplate.git
cd ComposeTemplate
./gradlew create-new-app \
  -Pargs='com.example.myapp,MyNewApp' \
  -PdataStrategy=remote \
  -q --console=plain
```

`dataStrategy` defaults to `remote` and accepts:

- `remote` — Retrofit/OkHttp and the auth sample; no Room
- `offline-first` — network, auth, and Room
- `local` — Room only; no network or auth
- `minimal` — no predefined network or database infrastructure

Use `-PwithSecrets=false` with a network-backed strategy to omit native secrets and hardening. Local and minimal outputs omit that network-dependent capability automatically.

Add a feature vertical with:

```bash
./gradlew scaffoldFeature -PfeatureName=user_profile
```

## Documentation

All documentation lives in **[`wiki/`](wiki/README.md)** — the single source of truth.

- [00 - Project Context](wiki/00-project-context.md)
- [01 - Module Topology and Build System](wiki/01-module-topology.md)
- [02 - Navigation and UI State](wiki/02-navigation-and-ui-state.md)
- [03 - Network and Auth Token Flow](wiki/03-network-and-auth.md)
- [04 - Secrets, Security and Hardening](wiki/04-secrets-and-hardening.md)
- [05 - Generator and Scaffolding Tooling](wiki/05-generator-and-scaffolding.md)
- [06 - Quality, Tests and CI](wiki/06-quality-tests-ci.md)
- [07 - Risks, Gaps and Open Questions](wiki/07-risks-and-gaps.md)
- [08 - Getting Started](wiki/08-getting-started.md)

## Requirements

JDK 17 and Android SDK (compileSdk 37 / targetSdk 36 / minSdk 26). NDK 27 with CMake is required only for outputs that retain secrets. Always use the Gradle wrapper.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Apache-2.0 — see [LICENSE](LICENSE).
