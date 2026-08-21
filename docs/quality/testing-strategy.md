# Testing Strategy

Testing strategy keeps architectural decisions sustainable.

## Tools

- JUnit
- MockK
- Truth
- kotlinx-coroutines-test
- AndroidX test dependencies

## Coroutine tests

Use `runTest`, not `runBlocking`.

```kotlin
@Test
fun `loads profile successfully`() = runTest {
    // test body
}
```

## Template-specific tests

The project should verify scaffold output, generated app creation, local secret exclusion, and native JNI behavior after rebranding.
