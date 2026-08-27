# Testing Strategy

ComposeTemplate treats testing as part of the architecture, not as an afterthought.

The project includes examples and build conventions that make tests consistent across modules and generated features.

## Problem

Android tests become difficult when layers are tightly coupled. If ViewModels directly call Retrofit services, UI state depends on DTOs, or repositories hide broad exceptions, tests need too much infrastructure to verify simple behavior.

ComposeTemplate's architecture makes smaller tests possible by separating domain contracts, data implementations, presentation state, and shared infrastructure.

## Tools

| Tool | Purpose |
|---|---|
| JUnit | unit test framework |
| MockK | mocking and verification |
| Truth | readable assertions |
| kotlinx-coroutines-test | coroutine testing with `runTest` |
| AndroidX test libraries | Android and instrumentation support |

## Test boundaries

### Domain tests

Domain tests should validate use cases and business rules with fake or mocked repository contracts.

They should not require Retrofit, Room, or Compose.

### Data tests

Data tests should validate repository implementations, mapping, API response handling, persistence behavior, and `BaseRepository` error mapping.

### Presentation tests

Presentation tests should validate ViewModel state transitions and one-shot events.

A ViewModel test should assert:

- initial state,
- state after user input,
- state after successful use case result,
- state after failure,
- emitted navigation or snackbar events.

## Coroutine testing

Use `runTest`, not `runBlocking`:

```kotlin
@Test
fun loadsProfileSuccessfully() = runTest {
    // test body
}
```

When testing `viewModelScope`, configure the main dispatcher in test setup.

## Template-specific tests

A template repository must test generated output, not only current source files.

ComposeTemplate CI validates:

- standard feature scaffolding,
- database-backed feature scaffolding,
- generated feature compilation,
- generated app creation,
- local secret exclusion.

## Testing generated code

Generated code should pass the same quality bar as hand-written code. If generated files fail tests, static analysis, or compilation, fix the generator rather than editing generated output manually in CI.

## Checklist

- [ ] domain use cases can be tested without Android framework dependencies.
- [ ] ViewModel tests use `runTest`.
- [ ] ViewModel tests configure the main dispatcher when needed.
- [ ] repository tests cover success and expected error cases.
- [ ] generated features compile in CI.
- [ ] generated apps are smoke-tested in CI.
- [ ] test dependencies are centralized through convention plugins.

## Repository references

- `build-logic/convention/TestConventionPlugin.kt`
- `core/network/src/test`
- `feature/*/*/src/test`
- `.github/workflows/ci.yml`
