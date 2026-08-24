# Static Analysis with Ktlint and Detekt

Static analysis turns code quality into an automated system instead of a review preference.

## Problem

Without automated checks, pull requests spend time on formatting debates, style drift, and preventable code smells.

## ComposeTemplate approach

ComposeTemplate uses:

- Ktlint for formatting
- Detekt for code quality
- a static-analysis convention plugin
- CI enforcement

```bash
./gradlew ktlintCheck
./gradlew detekt
```

## Takeaway

Static analysis is not about style perfection. It is about making quality consistent and reviewable at scale.
