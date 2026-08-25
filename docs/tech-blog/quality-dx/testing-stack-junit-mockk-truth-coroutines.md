# Testing Stack: JUnit, MockK, Truth and Coroutine Tests

## Who this article is for

This article is for Android developers writing unit tests for ViewModels, use cases, repositories, and coroutine-based code.

## What you will learn

- What each testing library is responsible for
- Why coroutine tests need special handling
- When to use mocks vs fakes
- How a convention plugin improves test consistency

## Testing stack roles

ComposeTemplate standardizes common test tools:

- JUnit for test structure,
- MockK for mocks and verification,
- Truth for readable assertions,
- kotlinx-coroutines-test for coroutine execution control,
- AndroidX test libraries for Android-related tests.

## Coroutine testing

Coroutine tests should use `runTest`:

```kotlin
@Test
fun loadsData() = runTest {
    // test body
}
```

`runTest` provides a test scheduler and avoids many timing problems associated with real dispatchers.

ViewModel tests may need a main dispatcher rule when `viewModelScope` is involved.

## MockK vs fakes

Mocks are useful for verifying interactions. Fakes are useful when a dependency has meaningful behavior.

Use mocks for small isolated collaborations. Use fakes when the test should exercise realistic state changes.

## Truth assertions

Truth makes assertions readable:

```kotlin
assertThat(result).isEqualTo(expected)
```

Readable assertions make failures easier to understand.

## ComposeTemplate approach

The test convention plugin applies common test dependencies so feature modules do not reinvent test setup.

## Common mistakes

- Using real dispatchers in unit tests.
- Over-mocking domain logic.
- Testing implementation details instead of behavior.
- Forgetting to test one-shot events separately from state.

## Production checklist

- [ ] Coroutine tests use `runTest`.
- [ ] Main dispatcher is controlled in ViewModel tests.
- [ ] Assertions are readable.
- [ ] Mocks and fakes are chosen intentionally.
- [ ] Test dependencies are centralized.

## Summary

A shared testing stack improves developer experience and review quality. ComposeTemplate standardizes test dependencies so feature tests are easier to write consistently.