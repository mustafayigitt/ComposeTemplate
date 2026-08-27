# Retrofit, OkHttp and Network Layer Design

## Who this article is for

This article is for Android developers designing a reusable network layer for modular applications.

## What you will learn

- what Retrofit and OkHttp each own
- why network behavior should be centralized
- how repository-level error mapping works
- why token handling and logging need careful boundaries

## The problem

Networking becomes inconsistent when each feature configures requests, errors, tokens, and logging independently.

Common issues include:

- duplicated Retrofit setup
- inconsistent error messages
- tokens injected from ViewModels
- sensitive headers logged
- DTOs leaking into UI state
- every feature handling HTTP failures differently

## Why this matters for Android projects

Network behavior affects reliability, security, debugging, and user experience. A multi-module app needs a shared network foundation with feature-specific repository implementations on top.

## Retrofit vs OkHttp

Retrofit defines API service interfaces and adapts HTTP calls into Kotlin-friendly methods.

OkHttp executes requests and owns low-level behavior such as interceptors, authentication hooks, connection configuration, logging, and certificate policies.

## ComposeTemplate's approach

ComposeTemplate centralizes shared network infrastructure in `core:network` and keeps feature-specific API calls inside feature data modules.

The auth feature demonstrates the pattern:

- `AuthService` defines API calls
- `AuthRepository` calls the service
- `BaseRepository.safeCall` maps expected failures
- domain receives `Result<AuthModel>` instead of Retrofit responses

## Error mapping

`BaseRepository.safeCall` maps successful responses and expected failures into explicit `Result` values. It handles HTTP status failures, empty bodies, `HttpException`, and `IOException`.

It intentionally avoids catching generic `Exception` by default so programming mistakes are not silently hidden.

## Token boundaries

Token storage and refresh contracts should live behind abstractions. ViewModels should not read or write tokens directly.

The auth feature exposes domain contracts while data implementations handle persistence and service calls.

## Safe logging

Network logs are useful in development, but release builds should avoid body logging and redact sensitive values.

Treat authorization headers, cookies, API keys, and tokens as sensitive.

## Design trade-offs

A centralized network layer requires shared conventions and careful abstraction. Too little abstraction causes duplication; too much creates an inflexible platform layer.

ComposeTemplate keeps shared concerns in `core:network` and feature behavior in feature data modules.

## Production checklist

- [ ] Retrofit service interfaces stay in data/infrastructure layers
- [ ] ViewModels do not call Retrofit directly
- [ ] expected network failures map to explicit results
- [ ] broad exception swallowing is avoided
- [ ] token storage is centralized behind contracts
- [ ] release logging does not expose sensitive values
- [ ] DTOs do not leak into UI state

## Takeaways

- A network layer is more than Retrofit setup.
- Error mapping belongs at the data boundary.
- Tokens and logging are security-sensitive architecture decisions.
