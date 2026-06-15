# ComposeTemplate — Conventions for AI Agents

## Project Structure
```
app/                          # Main application module
benchmark/                    # Startup benchmarks
build-logic/convention/       # Convention plugins (Gradle)
core/
  common/                     # Shared utilities (dispatchers, DI)
  data/                       # Data layer (PreferencesManager, etc.)
  navigation/                 # NavigationManager, ScreenRegistry, INavigationItem
  network/                    # Retrofit, OkHttp, AuthInterceptor, BaseRepository
  secrets/                    # NDK-based secret management
  ui/                         # Shared UI components, theme, BaseViewModel
feature/
  {name}/
    data/                     # Repository implementations, DTOs
    domain/                   # Use cases, domain models
    navigation/               # Feature-specific NavKey routes, DI module
    presentation/             # ViewModels, Compose screens, ScreenProvider
```

## Conventions

### Convention Plugins
- Feature sub-modules must use the dedicated convention plugins for infrastructure dependencies. Inter-module feature dependencies (e.g., data → domain) are added manually in `build.gradle.kts`:
  - `composetemplate.feature.domain` — provides `:core:common`, `:core:network`
  - `composetemplate.feature.data` — provides `:core:common`, `:core:data`, `:core:network`, `:core:secrets`
  - `composetemplate.feature.navigation` — provides `:core:common`, `:core:navigation`, material-icons-core, compose
  - `composetemplate.feature.presentation` — provides `:core:common`, `:core:ui`, `:core:navigation`, material-icons-core, compose, hilt, test deps
- Convention plugins are registered in `build-logic/convention/build.gradle.kts`.
- To add a new plugin, create the `.kt` file in `build-logic/convention/src/main/kotlin/...` and register it in `build.gradle.kts`.

### Secrets
- Configured via `secrets.properties` in project root (see `secrets.properties.example`).
- Validated by `./gradlew validateSecrets`.
- Passed to NDK via CMake `-D` flags in `AndroidLibraryNativeConventionPlugin`.
- SecretManager uses `BuildConfig.DEBUG` to select debug/release keys.

### Network Layer
- `BaseRepository` catches only `IOException` and `HttpException` (not generic `Exception`).
- Network logging is `BODY` in debug builds, `NONE` in release.
- `AuthInterceptor` handles token injection.
- `SecretManager.getBaseUrl()` provides the Retrofit base URL.

### Navigation
- `NavigationManager` manages back stack via `StateFlow<List<INavigationItem>>`.
- `navigateBack()` keeps the last stack entry (doesn't empty stack).
- `navigateOver(route, over)` replaces the stack from the position of `over` route.
- `navigateToTop(route)` replaces everything after start destination.
- Bottom bar items are registered in feature navigation DI modules via `@Provides @IntoMap @StringKey("N")` and sorted by their map key.
- Feature navigation routes implement `INavigationItem` (extending `NavKey`); bottom bar items implement `IBottomBarItem` which adds `icon: @Composable () -> Unit`.
- Route objects use `kotlinx.serialization.Serializable`.
- Screens are registered per-feature via `IScreenProvider` (multibound `@Binds @IntoSet` in the presentation DI module).
- `ScreenRegistry` collects `Set<IScreenProvider>` and renders the first matching screen.
- Navigation DI modules should only provide bindings needed (`@IntoMap @StringKey` for bottom bar, `@Singleton @Provides` for start destination). Do NOT add `@IntoSet INavigationItem` bindings — they are unused.

### Architecture
- Clean Architecture: data → domain → presentation (dependency inward).
- Hilt for DI.
- ViewModels must extend `BaseViewModel<UiState, Event>` and declare `override val _uiState = MutableStateFlow(UiState(...))`.
- Use `updateState { it.copy(...) }` for state mutations (not direct `_uiState.value = ...`).
- Use `sendEvent(event)` for one-shot UI events (snackbar, navigation).
- Use `collectAsStateWithLifecycle()` in composables (not `collectAsState()`).
- Use `runTest` for coroutine tests (never `runBlocking`).

### Testing
- Unit tests in `src/test/` matching production package.
- `runTest` for coroutine testing.
- `mockk` for mocking.
- `com.google.common.truth` for assertions.
- ViewModel tests must set `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@Before` when testing `viewModelScope.launch`.

### Adding a New Feature
Create 4 sub-modules under `feature/{name}/`:
- `data/` — `composetemplate.feature.data` plugin
- `domain/` — `composetemplate.feature.domain` plugin
- `navigation/` — `composetemplate.feature.navigation` plugin + kotlinx.serialization
- `presentation/` — `composetemplate.feature.presentation` plugin
Register each in `settings.gradle.kts` and add all 4 as `implementation(project(...))` entries in `app/build.gradle.kts`.
If the feature is a bottom bar tab, add `@Provides @IntoMap @StringKey("N")` binding in the navigation DI module.
Add an `IScreenProvider` in the presentation DI module to register the screen with `ScreenRegistry`.