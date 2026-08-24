# Building an Android Template Generator with Gradle

A template generator must do more than copy files. It must produce a clean, independent project that no longer depends on the original template repository.

## Problem

Manual clone-and-rename flows are fragile. Package names appear in Kotlin, XML, Gradle, properties, resources, manifests, and native code.

## ComposeTemplate approach

`CreateNewAppPlugin` automates project generation:

- copies the template to a sibling directory,
- rewrites package name and app name,
- moves source directories,
- removes template-specific generator code,
- excludes `.git`, `.gradle`, `.idea`, `local.properties`, `secrets.properties`, and build outputs.

## Takeaway

A reliable template generator is a product feature, not a convenience script.
