# ComposeTemplate Development Guide

This guide explains how to quickly add new features and extend the project using ComposeTemplate.

## 1. Adding a New Feature

Use the scaffolding task to add a new feature:

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

This command automatically:
- Creates the `feature/settings` directory and 4 sub-modules.
- Includes the modules in `settings.gradle.kts`.
- Adds module dependencies to `app/build.gradle.kts`.
- Generates the base navigation route (`SettingsRoute`).

## 2. Navigation Registration

The `scaffoldFeature` command creates the route in the `navigation` module and the `IScreenProvider` binding in the `presentation` module. If adding manually, follow this pattern:

```kotlin
class SettingsScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(route: INavigationItem, navigationManager: INavigationManager): Boolean =
        when (route) {
            is SettingsRoute -> {
                SettingsScreen(navigationManager)
                true
            }
            else -> false
        }
}
```

### Bottom Bar Tabs
If the feature should appear in the bottom navigation bar, register it in the navigation DI module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SettingsNavigationModule {
    @Provides
    @IntoMap
    @StringKey("4")
    fun provideBottomBarItem(): IBottomBarItem = SettingsRoute
}
```

### Cross-Feature Navigation
Feature presentation modules may depend on other feature navigation modules (e.g., `:feature:home:presentation` depending on `:feature:list:navigation`). This is intentional for the template — it keeps navigation targets directly resolvable at compile time. For larger projects, consider abstracting navigation targets through a shared navigation abstraction or resolving dependencies at the app level.

## 3. Architecture Standards

### ViewModel Structure
All ViewModels must extend `BaseViewModel`:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor() : BaseViewModel<MyUiState, MyEvent>() {
    override val uiStateInternal = MutableStateFlow(MyUiState())

    fun doSomething() {
        updateState { it.copy(isLoading = true) }
        // ...
    }
}
```

### UI State and Events
- **UiState:** A single data class representing all screen state.
- **Event:** Channel-based system for one-shot events like snackbar display and navigation.
- **Route/UI Separation:** The Route composable wires the ViewModel, lifecycle collection, events, and navigation. The plain UI composable receives only immutable state and callbacks.

### Feature Complexity Levels
Features in the template intentionally vary in complexity to demonstrate different patterns:

| Feature | Complexity | Demonstrates |
|---------|-----------|-------------|
| `auth` | **Full** | Complete Clean Architecture with API service, full data/domain/presentation layers, token refresh, tests at all layers |
| `splash` | **Full** | Repository pattern with start destination logic, tests at all layers |
| `profile` | **Medium** | Preferences-based data layer, multiple use cases, dynamic theme/language switching |
| `home` | **Minimal** | Bottom-bar tab with the minimum correct structure (data class UiState, BaseViewModel, ScreenProvider) |
| `detail` | **Minimal** | Parameterized route with ID extraction in ScreenProvider |
| `list` | **Minimal** | List screen with navigation to detail |
| `search` | **Minimal** | Search field with filterable list |
| `onboarding` | **Medium** | Pager-based UI with repository, pass-through use case |

For new features, reference the feature at your desired complexity level. The `scaffoldFeature` task generates a minimal structure by default.

## 4. Secret Management

To add a new API key or client config value:
1. Add the key to `secrets.properties`.
2. Validate with `./gradlew validateSecrets`.
3. Define a new method in `SecretManager.kt`.
4. Implement the JNI method in `native-lib.cpp`.
5. For release, check `./gradlew hardeningReport` and `scanApkForSecrets` output.

Note: Values placed in the client are never absolutely secure; this architecture provides a defense-in-depth layer that increases the cost of reverse engineering through native obfuscation, runtime integrity checks, and MITM protections.

## 5. UI Components

Shared components are in the `:core:ui` module:
- `AppButton`: Standard button.
- `AppTextField`: Text input with icon slots and error state.
- `AppLoading`: Loading indicator (inline/fullscreen modes).
- `AppErrorState`: Ready-made error screen with optional retry.
- `AppEmptyState`: Empty state with icon and message.
- `AppCard`: Card with configurable elevation.
- `AppTopBar`: Center-aligned top bar with navigation and actions.
- `AppDialog`: Alert dialog with confirm/dismiss buttons.
- `AppNoInternetBanner`: Animated network status banner.
- `AppSkeleton`: Shimmer loading skeletons.
- `AppSearchField`: Search input with clear button.
- `AppSurface`: Themed surface wrapper.
- `AppListItem`: List item with leading/trailing slots.
- `AppBadge`, `AppChip`, `AppDivider`, `AppIconButton`, `AppAvatar`

## 6. Onboarding and Flow Control

At app launch, `:feature:splash` determines the start destination. The onboarding flow is separated into data/domain/navigation/presentation under `:feature:onboarding`.

When adding a new onboarding step, typically update:
- `feature/onboarding/presentation/.../OnboardingUiState.kt`
- `feature/onboarding/presentation/.../OnboardingRoute.kt`
- `feature/onboarding/presentation/src/main/res/values/strings.xml`
- If needed, `feature/onboarding/domain` use cases and `feature/onboarding/data` repository implementation

## 7. Permission Management

Use `:core:permission` module to easily manage permissions:

```kotlin
PermissionRequired(
    permission = Manifest.permission.CAMERA,
    rationaleTitle = "Camera permission",
    rationaleMessage = "Camera access is needed to scan codes.",
    permanentlyDeniedTitle = "Permission disabled",
    permanentlyDeniedMessage = "Enable camera permission from settings.",
    onPermissionGranted = {
        CameraContent()
    },
    onPermissionDenied = { status, requestPermission ->
        PermissionDeniedContent(
            status = status,
            onRequestPermission = requestPermission,
        )
    },
)
```

## 8. Premium UI and Google Play Features

### Shimmer (Loading Effect)
Add `Modifier.shimmer()` or use `AppListSkeleton` for professional shimmer loading placeholders.

### Dynamic Theme
Dark/light mode is controlled via `PreferencesManager.isDarkModeFlow`. Toggle it via `ProfileViewModel.toggleTheme()`. The theme updates instantly without activity recreation.

### Dynamic Language
Language switching is handled by `LocaleManager` in `:core:data`. It applies the locale via `AppCompatDelegate.setApplicationLocales()` and automatically restores the saved language on app start. On Android API 33+, switching is instant; on older APIs, the activity recreates.

### In-App Review
Inject `ReviewManager` to request a rating from users:
```kotlin
reviewManager.requestReview(activity)
```

### App Update
Use `UpdateManager` to handle both Google Play updates and Remote Config-based "Force Update" logic.

## 9. Static Analysis and Quality

Before submitting code, always run:
```bash
./gradlew ktlintCheck detekt
./gradlew testDebugUnitTest assembleDebug :app:assembleRelease
```
