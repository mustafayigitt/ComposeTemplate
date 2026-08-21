# UI State Management

UI state management separates durable render state from one-shot events.

## Pattern

ViewModels follow `BaseViewModel<UiState, Event>`.

- `UiState`: persistent state used to render the screen
- `Event`: one-shot side effects such as navigation or snackbar

## Example

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)
```

Use lifecycle-aware collection:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

## Checklist

- [ ] UiState is immutable.
- [ ] One-shot effects are modeled as Event.
- [ ] UI uses `collectAsStateWithLifecycle()`.
