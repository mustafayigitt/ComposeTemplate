# UI State and One-Shot Events in Jetpack Compose

Compose UI is easiest to reason about when persistent render state and one-shot effects are modeled separately.

## Problem

Navigation events, snackbars, loading flags, and form state often get mixed into one state object. This leads to repeated events, stale messages, and hard-to-test ViewModels.

## ComposeTemplate approach

ViewModels follow `BaseViewModel<UiState, Event>`.

- `UiState` represents durable render state.
- `Event` represents one-shot effects like navigation or snackbar messages.

```kotlin
data class LoginUiState(
    val email: String = "",
    val isLoading: Boolean = false,
)
```

## Takeaway

Good Compose state management is less about APIs and more about separating what the UI is from what the UI does once.
