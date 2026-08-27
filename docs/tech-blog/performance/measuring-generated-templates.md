# Measuring Generated Templates, Not Just Apps

## Who this article is for

This article is for maintainers of Android templates and mobile platform foundations.

## What you will learn

- why template performance matters
- why generated apps should keep performance tooling
- how Baseline Profiles and Macrobenchmark support template quality
- what to measure after generation

## The problem

A template can include good performance tooling but still fail downstream if generated apps do not preserve it or if generation changes startup behavior.

Template maintainers must think about generated output quality, not only current app quality.

## Why this matters for Android projects

Generated apps inherit architecture, dependencies, navigation, security checks, and startup work. If those foundations add overhead, every downstream app pays the cost.

## ComposeTemplate's approach

ComposeTemplate includes both `baselineprofile` and `benchmark` modules and wires ProfileInstaller into the app. Generated apps should retain the ability to profile and benchmark startup.

## What to measure

Measure:

- cold startup
- first screen rendering
- start destination logic
- navigation to primary tabs
- onboarding/auth flows when relevant
- impact of security checks on startup

## Design trade-offs

Keeping benchmark infrastructure in generated apps adds modules and device requirements. Removing it makes generated projects simpler but weakens performance visibility.

For production templates, visibility is usually worth the cost.

## Production checklist

- [ ] generated apps retain performance modules or documented equivalents
- [ ] startup is benchmarked after generation
- [ ] Baseline Profile rules match generated navigation paths
- [ ] benchmark build type remains release-like
- [ ] performance regressions are reviewed before template releases

## Takeaways

- Template performance is inherited by generated apps.
- Generated output should be measured directly.
- Performance tooling belongs in the foundation, not only in late-stage optimization.
