# Adding a New Feature

Use `scaffoldFeature` for a generated starting point, then adapt the generated code.

```bash
./gradlew scaffoldFeature -PfeatureName=settings
./gradlew scaffoldFeature -PfeatureName=settings -PwithDatabase=true
```

## Manual checklist

- [ ] Create data/domain/navigation/presentation modules.
- [ ] Apply correct convention plugins.
- [ ] Add includes to `settings.gradle.kts`.
- [ ] Add dependencies to `app/build.gradle.kts`.
- [ ] Create route and ScreenProvider.
- [ ] Add Hilt bindings and tests.
