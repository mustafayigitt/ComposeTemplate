# Project Philosophy

ComposeTemplate is built around one idea:

> Starting a new Android project should not require rebuilding architecture, build logic, security checks, and quality gates from scratch.

## Why a template generator?

A sample app says: “This is one way to do it.”

A template generator says: “Generate this structure with your package name, app name, and project identity.”

`create-new-app` is therefore a core capability.

## Principles

1. Boundaries should be visible at build level.
2. Generator behavior must be tested.
3. Security claims must be honest.
4. Adding a feature should be easy.
5. Build logic should be centralized.

## Trade-offs

- More modules at the start
- A steeper learning curve
- Gradle convention plugins require build-system knowledge
- Native secret obfuscation adds NDK/CMake requirements
- Generator code needs maintenance

For medium and large apps, these costs are often outweighed by clearer boundaries, better testing, consistent build logic, and safer refactoring.
