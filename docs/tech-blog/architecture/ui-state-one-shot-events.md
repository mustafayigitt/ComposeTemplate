# UI State and One-Shot Events in Jetpack Compose

## Who this article is for

This article is for Compose developers building screens with ViewModels, flows, navigation events, loading states, and user actions.

## What you will learn

- Why render state and one-shot effects should be separate
- How `StateFlow` and event flows play different roles
- How this maps to Compose screens
- Common mistakes that cause repeated navigation or stale snackbar messages

## The problem

UI has two kinds of information.

The first kind is durable state:

- current email text,
- loading flag,
- list of items,
- selected tab,
- validation errors.

The second kind is a one-time effect:

- navigate to another screen,
- show a snackbar,
- open a dialog once,
- trigger a toast,
- request focus.

If both are stored in one persistent state object, effects can repeat after recomposition, configuration change, or process recreation.

## Mental model

State describes what the UI should look like.

Events describe what should happen once.

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginEvent {
    data object NavigateHome : LoginEvent
    data class ShowMessage(val message: String) : LoginEvent
}
```

## ComposeTemplate approach

ComposeTemplate uses a `BaseViewModel<UiState, Event>` pattern. The state is exposed as observable state for rendering. Events are emitted separately for one-time effects.

This gives every feature the same structure:

- ViewModel owns state transitions.
- Compose renders state.
- Compose collects events in effect handlers.
- Navigation is not stored as durable state.

## Lifecycle-aware collection

State should be collected with lifecycle awareness:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

Events should be collected in a `LaunchedEffect` or a lifecycle-aware helper depending on the project conventions.

## Common mistakes

### Storing navigation in state

A flag like `shouldNavigate = true` can trigger navigation repeatedly unless it is reset carefully.

### Using nullable message as an event

`snackbarMessage: String?` often becomes stale state. Prefer one-shot events for transient messages.

### Letting composables contain business decisions

Composable functions should render and forward user actions. ViewModels should decide state transitions.

## Production checklist

- [ ] UI state is immutable.
- [ ] One-shot effects are modeled separately.
- [ ] State collection is lifecycle-aware.
- [ ] Navigation is emitted as an event.
- [ ] ViewModel tests verify state transitions and emitted effects.

## Summary

Compose state management becomes easier when durable state and transient effects are separated. ComposeTemplate standardizes this pattern so feature screens behave consistently and remain testable.