# UI State Management

ComposeTemplate separates durable render state from one-shot UI events.

The pattern is standardized through `BaseViewModel<S, E>` in `core:ui`.

## Problem

Compose screens need stable state for rendering and separate mechanisms for one-time effects such as navigation and snackbars.

If these concerns are mixed, common bugs appear:

- snackbars are shown again after recomposition,
- navigation events repeat after configuration changes,
- mutable state is changed from multiple places,
- ViewModels expose implementation details,
- tests become harder to reason about.

## BaseViewModel pattern

`BaseViewModel<S, E>` defines:

- an internal `MutableStateFlow<S>`,
- a public `StateFlow<S>`,
- a `Channel<E>` for one-shot events,
- `updateState { }` for state mutation,
- `sendEvent(event)` for one-shot event emission.

Conceptually:

```kotlin
abstract class BaseViewModel<S, E> : ViewModel() {
    protected abstract val uiStateInternal: MutableStateFlow<S>

    val uiState: StateFlow<S> = uiStateInternal.asStateFlow()

    val events = Channel<E>().receiveAsFlow()

    protected fun updateState(update: (S) -> S)
    protected fun sendEvent(event: E)
}
```

## Durable state

`UiState` is the durable state required to render the screen.

Example:

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)
```

State should be immutable and replaced through `copy`.

```kotlin
fun onEmailChanged(email: String) {
    updateState { it.copy(email = email) }
}
```

## One-shot events

Events represent effects that should be consumed once:

- navigation,
- snackbar messages,
- dialogs triggered by an action,
- permission prompts.

Example:

```kotlin
sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
}
```

A ViewModel emits events with `sendEvent(event)` instead of putting one-time commands into durable state.

## Lifecycle-aware collection

Compose screens collect state with lifecycle awareness:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

This avoids collecting while the UI is not in an active lifecycle state.

## ViewModel responsibilities

ViewModels should:

- call domain use cases,
- translate results into UI state,
- emit one-shot events,
- avoid direct Retrofit, DAO, or DTO usage,
- keep business rules out of Composables.

## Testing considerations

Coroutine-based ViewModels should be tested with `runTest`. When testing code that uses `viewModelScope`, configure the main dispatcher in tests.

Use assertions against `uiState` and collect events explicitly when testing one-shot behavior.

## Common mistakes

### Putting events in state

Avoid storing navigation commands as persistent state. They may be consumed repeatedly.

### Directly mutating internal state

Prefer `updateState { it.copy(...) }` over assigning to `uiStateInternal.value` from scattered locations.

### Using `collectAsState()` instead of lifecycle-aware collection

Prefer `collectAsStateWithLifecycle()` in Composables.

## Checklist

- [ ] `UiState` is immutable.
- [ ] one-shot effects are modeled as events.
- [ ] ViewModels extend `BaseViewModel<UiState, Event>`.
- [ ] state updates go through `updateState`.
- [ ] one-shot events go through `sendEvent`.
- [ ] Composables use `collectAsStateWithLifecycle()`.
- [ ] ViewModel tests use coroutine test utilities.

## Repository references

- `core/ui/BaseViewModel.kt`
- `feature/auth/presentation/LoginViewModel.kt`
- `feature/profile/presentation/ProfileViewModel.kt`
- `feature/splash/presentation/SplashViewModel.kt`
