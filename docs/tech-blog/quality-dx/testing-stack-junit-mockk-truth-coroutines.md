# Testing Stack: JUnit, MockK, Truth and Coroutine Tests

## Who this article is for

This article is for Android developers testing multi-module Kotlin and Compose applications.

## What you will learn

- why testing strategy follows architecture
- where JUnit, MockK, Truth, and coroutine testing fit
- how ViewModel and repository tests should be structured
- why template repositories must test generated output

## The problem

Tests become expensive when architecture is tightly coupled. If ViewModels know Retrofit, UI state exposes DTOs, or domain logic depends on Android framework classes, even simple tests require large setup.

## Why this matters for Android projects

Good tests reinforce architecture. Domain tests should be small. ViewModel tests should verify state transitions. Repository tests should cover mapping and expected failures. Generator smoke tests should prove generated code compiles.

## ComposeTemplate's stack

ComposeTemplate standardizes:

- JUnit for test structure
- MockK for mocks
- Truth for assertions
- kotlinx-coroutines-test for coroutine code
- AndroidX test dependencies where needed

These dependencies are centralized through convention plugins.

## Coroutine tests

Use `runTest` instead of `runBlocking`:

```kotlin
@Test
fun loadsProfileSuccessfully() = runTest {
    // test body
}
```

ViewModel tests that use `viewModelScope` should configure the main dispatcher during setup.

## What to test

### Domain

Test use cases with fake repositories.

### Data

Test repository mapping, success responses, empty bodies, HTTP errors, and IO failures.

### Presentation

Test initial state, user actions, loading state, success state, failure state, and one-shot events.

### Generator behavior

Test that generated features and generated apps compile and exclude local-only files.

## Design trade-offs

Mock-heavy tests can become brittle. Prefer fakes for domain contracts when that produces clearer tests. Use mocks for interaction verification when needed.

## Production checklist

- [ ] domain tests avoid Android framework dependencies
- [ ] coroutine tests use `runTest`
- [ ] ViewModel tests configure main dispatcher when needed
- [ ] repository tests cover success and expected errors
- [ ] generated feature compile checks run in CI
- [ ] generated app smoke checks run in CI

## Takeaways

- Testing strategy should mirror architecture boundaries.
- Coroutine tests need coroutine-aware tooling.
- Template repositories must test generated output, not only current source.
