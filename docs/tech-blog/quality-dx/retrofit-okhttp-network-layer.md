# Retrofit, OkHttp and Network Layer Design

## Who this article is for

This article is for Android developers designing a reusable network layer for feature-based apps.

## What you will learn

- What Retrofit and OkHttp are responsible for
- Why interceptors and authenticators should be separated
- How network error handling should be modeled
- Why logging must be safe by default

## Network layer responsibilities

A production network layer usually handles:

- request creation,
- serialization,
- authentication headers,
- token refresh,
- error mapping,
- connectivity awareness,
- logging and redaction,
- base URL configuration.

Mixing these concerns inside feature repositories leads to duplication and inconsistent behavior.

## Retrofit vs OkHttp

Retrofit defines API interfaces and maps HTTP calls into Kotlin-friendly service methods.

OkHttp executes HTTP requests and provides lower-level hooks such as interceptors, authenticators, connection settings, and certificate pinning.

They solve different layers of the network stack.

## Interceptor vs Authenticator

An interceptor decorates or observes requests. For example, `AuthInterceptor` can attach an access token.

An authenticator reacts to authentication failures. For example, `TokenAuthenticator` can respond to HTTP 401 by refreshing tokens and retrying.

Keeping these separate prevents request decoration from becoming recovery logic.

## ComposeTemplate approach

ComposeTemplate centralizes network infrastructure in `core:network`, including:

- Retrofit,
- OkHttp,
- AuthInterceptor,
- TokenAuthenticator,
- BaseRepository,
- NetworkMonitor,
- sensitive header redaction.

Feature data modules consume this shared infrastructure instead of rebuilding it.

## Error handling

Network errors should be mapped into domain-friendly results. A base repository can catch expected errors such as IO and HTTP failures, but it should avoid swallowing programming errors with overly broad catches.

## Safe logging

HTTP logging is useful in debug builds but dangerous in release builds. Sensitive headers such as `Authorization`, cookies, API keys, and auth tokens should be redacted.

## Common mistakes

- Logging request/response bodies in release.
- Refreshing tokens inside every repository.
- Treating all exceptions the same.
- Exposing DTOs directly to UI.
- Forgetting offline/connectivity behavior.

## Production checklist

- [ ] Auth header injection is centralized.
- [ ] Token refresh is handled separately from request decoration.
- [ ] Sensitive headers are redacted.
- [ ] Release logging is disabled or safe.
- [ ] Network errors are mapped intentionally.
- [ ] Feature modules do not duplicate client setup.

## Summary

A good network layer is not just Retrofit setup. It is a boundary for authentication, error handling, logging, connectivity, and security expectations.