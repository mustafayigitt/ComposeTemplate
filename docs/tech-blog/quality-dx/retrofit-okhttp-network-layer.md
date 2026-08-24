# Retrofit, OkHttp and Network Layer Design

A reusable Android template needs a network layer that is practical, secure by default, and easy for feature modules to consume.

## Problem

Network code often becomes scattered across features. Token injection, logging, error handling, and refresh behavior may be duplicated or implemented inconsistently.

## ComposeTemplate approach

ComposeTemplate centralizes network infrastructure in `core:network`.

Key pieces:

- Retrofit
- OkHttp
- AuthInterceptor
- TokenAuthenticator
- BaseRepository
- NetworkMonitor
- sensitive header redaction

## Takeaway

A good network layer is not just Retrofit setup. It is a boundary for auth, errors, logging, connectivity, and security expectations.
