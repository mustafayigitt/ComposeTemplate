# KSP, Hilt and Build Logic Integration

Dependency injection in a multi-module Android project should be consistent, repeatable, and easy to apply to new modules.

## Problem

Without shared build logic, every module repeats Hilt and KSP setup. This creates config drift and makes new modules harder to add.

## ComposeTemplate approach

ComposeTemplate centralizes DI setup in convention plugins. Feature modules apply the right plugin for their layer, and Hilt/KSP wiring is handled consistently.

## Hilt multibinding

ComposeTemplate uses Hilt multibinding for scalable feature registration:

- Screen providers can be contributed with `@IntoSet`.
- Bottom-bar items can be contributed with `@IntoMap`.
- App-level code does not need to know every concrete feature implementation.

## Takeaway

DI setup is build architecture. Centralizing it keeps feature modules predictable and easier to scale.
