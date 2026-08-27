# UI State and One-Shot Events in Jetpack Compose

## Who this article is for

This article is for Compose developers who want predictable ViewModel state and one-time side-effect handling.

## What you will learn

- why durable UI state and one-shot events should be separated
- how `StateFlow` fits Compose rendering
- why channels are useful for events
- how ComposeTemplate standardizes ViewModels with `BaseViewModel`
- what mistakes to avoid in state/event handling

## The problem

Compose screens recompose. Configuration can change. Flows can be collected again. If one-time actions are stored as durable state, they may run more than once.

Common symptoms include:

- snackbar messages appearing again after rotation
- navigation happening twice
- events being lost or replayed unexpectedly
- ViewModels exposing mutable state directly
- UI code mutating state instead of rendering it

## Why this matters for Android projects

State management is one of the first places where small apps become hard to maintain. A production template should give every feature the same mental model for rendering state and emitting effects.

## Common approaches

### Everything in one state object

Simple, but one-shot commands can be accidentally replayed.

### Callback-heavy UI

Can work for small screens, but behavior becomes scattered as flows grow.

### State plus event stream

Durable screen state is modeled separately from one-time effects. ComposeTemplate uses this approach.

## ComposeTemplate's approach

Every ViewModel extends:

```kotlin
BaseViewModel<UiState, Event>
```

The base class provides:

- internal `MutableStateFlow<S>`
- public `StateFlow<S>`
- `Channel<E>`-backed event flow
- `updateState { }`
- `sendEvent(event)`

## Implementation walkthrough

A UI state describes what the screen renders:

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)
```

The ViewModel updates state immutably:

```kotlin
fun onEmailChanged(email: String) {
    updateState { it.copy(email = email) }
}
```

An event describes a one-shot effect:

```kotlin
sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
}
```

The ViewModel emits it:

```kotlin
sendEvent(ProfileEvent.NavigateToLogin)
```

Compose screens collect state with lifecycle awareness:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

## Design trade-offs

A separate event stream adds a small amount of ceremony, but it prevents a common class of recomposition and lifecycle bugs.

For generated features, the pattern also creates consistency across the codebase.

## Production checklist

- [ ] screen state is immutable
- [ ] one-shot effects are not stored as durable state
- [ ] ViewModels expose read-only state
- [ ] state changes use `updateState`
- [ ] events use `sendEvent`
- [ ] Composables use lifecycle-aware collection
- [ ] ViewModel tests cover state transitions and events

## Takeaways

- UI state should describe what to render.
- Events should describe what should happen once.
- A shared ViewModel base class keeps feature behavior consistent.
