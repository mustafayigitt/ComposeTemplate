# Measuring Generated Templates, Not Just Apps

Template performance matters because generated apps inherit the template’s defaults.

## Problem

A template can look clean but generate apps with slow startup, unnecessary dependencies, or poorly configured performance tooling.

## ComposeTemplate approach

ComposeTemplate includes performance modules from the beginning:

- `baselineprofile`
- `benchmark`

This makes generated apps performance-aware on day one.

## Takeaway

For template repositories, performance should be measured where users experience it: in the generated app.
