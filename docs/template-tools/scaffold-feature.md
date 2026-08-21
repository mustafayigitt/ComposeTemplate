# Scaffold Feature

`scaffoldFeature` turns feature setup into a repeatable generator workflow.

## Commands

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

## Generated structure

```text
feature/settings/
├── data/
├── domain/
├── navigation/
└── presentation/
```

## Checklist

- [ ] Four modules were generated.
- [ ] `settings.gradle.kts` was updated.
- [ ] App dependency wiring was added.
- [ ] ScreenProvider resolves correctly.
- [ ] Generated code was adapted to real needs.
