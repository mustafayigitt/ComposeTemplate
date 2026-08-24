# Testing Stack: JUnit, MockK, Truth and Coroutine Tests

A production template needs a predictable testing stack that feature modules can reuse.

## Problem

Without a standard test setup, each module invents its own testing style. Coroutine tests become flaky, assertions become inconsistent, and ViewModel behavior becomes hard to verify.

## ComposeTemplate approach

ComposeTemplate standardizes testing through a test convention plugin and common dependencies:

- JUnit
- MockK
- Truth
- kotlinx-coroutines-test
- AndroidX test libraries

## Takeaway

A shared testing stack is part of developer experience. It makes new feature tests easier to write and easier to review.
