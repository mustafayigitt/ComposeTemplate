# Retrofit, OkHttp and Network Layer Design

## Who this article is for

This article is for Android developers designing a reusable network layer for modular applications.

## What you will learn

- What Retrofit and OkHttp each do
- Why interceptors and authenticators are different
- How token refresh should be centralized
- Why safe logging and error mapping matter

## Network layer responsibilities

A production network layer handles request creation, serialization, authentication headers, refresh behavior, connectivity awareness, logging, certificate policy, and error mapping.

If each feature implements these independently, behavior becomes inconsistent.

## Retrofit vs OkHttp

Retrofit defines API interfaces and adapts HTTP calls into Kotlin-friendly service methods. OkHttp executes requests and provides low-level hooks: interceptors, authenticators, connection configuration, logging, and certificate pinning.

## Interceptor vs Authenticator

An interceptor decorates outgoing requests. For example, `AuthInterceptor` can attach an access token.

An authenticator reacts to authentication failures. For example, `TokenAuthenticator` can handle HTTP 401 by refreshing tokens and retrying once.

Mixing these responsibilities can create retry loops or duplicate refresh logic.

## Error handling

A base repository can map expected failures like IO and HTTP errors into domain results. It should avoid catching everything, because broad catches hide programming errors.

## Safe logging

HTTP body logging and auth headers are risky. Release builds should disable unsafe logging and redact sensitive headers such as `Authorization`, cookies, API keys, and auth tokens.

## ComposeTemplate approach

ComposeTemplate centralizes this in `core:network` with Retrofit, OkHttp, `AuthInterceptor`, `TokenAuthenticator`, `BaseRepository`, `NetworkMonitor`, and redaction rules.

## Production checklist

- [ ] Auth header injection is centralized.
- [ ] Token refresh is handled by an authenticator-like component.
- [ ] Refresh retry loops are prevented.
- [ ] Sensitive headers are redacted.
- [ ] Release logging is safe.
- [ ] Network errors are mapped intentionally.

## Summary

A good network layer is not just Retrofit setup. It is a boundary for authentication, errors, logging, connectivity, and security expectations.
