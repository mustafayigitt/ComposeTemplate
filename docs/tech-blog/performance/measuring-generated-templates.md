# Measuring Generated Templates, Not Just Apps

## Who this article is for

This article is for template maintainers and platform teams who want generated apps to inherit good performance defaults.

## What you will learn

- Why template performance matters
- Why measuring only the source app is incomplete
- How generated app validation can evolve
- How Baseline Profiles and Macrobenchmark fit template quality

## The problem

A template can build successfully and still generate apps with poor startup behavior, unnecessary dependencies, or broken performance tooling.

If users generate apps from the template, the generated output is the real product.

## Smoke testing vs performance testing

A smoke test asks:

```text
Does the generated app build?
```

A performance test asks:

```text
Does the generated app start and behave well?
```

Both matter.

## ComposeTemplate approach

ComposeTemplate includes:

- `baselineprofile`
- `benchmark`
- generator smoke tests
- scaffold smoke tests

This creates a foundation for future generated-app performance validation.

## What mature template performance could include

- Generate app in CI.
- Build release variant.
- Run startup Macrobenchmark.
- Generate Baseline Profile for generated package.
- Store performance reports as artifacts.
- Track regressions over time.

## Common mistakes

- Measuring only the template app.
- Ignoring generated package names in benchmark setup.
- Treating smoke tests as performance tests.
- Adding heavy startup dependencies to the template.

## Production checklist

- [ ] Template app has benchmark coverage.
- [ ] Generated app builds in CI.
- [ ] Generated app can run performance tests.
- [ ] Baseline profile paths survive package rename.
- [ ] Startup regressions are tracked over time.

## Summary

Template performance should be measured where users experience it: generated apps. ComposeTemplate includes the foundations needed to make this possible.