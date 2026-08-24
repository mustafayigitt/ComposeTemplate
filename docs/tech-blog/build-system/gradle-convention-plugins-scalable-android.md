# Gradle Convention Plugins for Scalable Android Projects

Multi-module Android projects often start clean and then slowly accumulate repeated Gradle configuration. Compose setup, Hilt wiring, test dependencies, static analysis, Room, KSP, and SDK settings begin to appear in every module.

ComposeTemplate avoids this by moving shared build logic into Gradle convention plugins.

## The problem

Without convention plugins, every module tends to repeat configuration. As module count grows, this creates config drift, copy-paste, inconsistent dependency setup, harder upgrades, and noisy module build files.

## ComposeTemplate’s approach

Build logic lives under:

```text
build-logic/convention/
```

Examples include:

- `composetemplate.android.application`
- `composetemplate.android.library`
- `composetemplate.android.hilt`
- `composetemplate.android.room`
- `composetemplate.feature.data`
- `composetemplate.feature.domain`
- `composetemplate.feature.navigation`
- `composetemplate.feature.presentation`
- `composetemplate.test`
- `composetemplate.static.analysis`

## Takeaway

Convention plugins are not just a cleanup tool. In a large Android project, they are part of the architecture.
