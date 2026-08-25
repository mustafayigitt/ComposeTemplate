# Building an Android Template Generator with Gradle

## Who this article is for

This article is for Android developers who want to build project generators, internal app templates, or reusable mobile platform foundations.

## What you will learn

- Why clone-and-rename is fragile
- What a generator must rewrite
- Why generated apps should remove generator code
- How CI should validate generated output

## The problem with manual rename

Android project identity is scattered across many places:

- package declarations,
- namespace and applicationId,
- manifest values,
- XML resources,
- Gradle scripts,
- source directory paths,
- native/JNI bindings,
- documentation.

Manual rename instructions are easy to get wrong. A template generator should perform these changes deterministically.

## Generator responsibilities

A reliable generator should:

1. copy the template,
2. rewrite package and app names,
3. move source directories,
4. exclude local-only files,
5. remove template-only tools,
6. verify the generated project can build.

## ComposeTemplate approach

ComposeTemplate implements generation through a Gradle task exposed by `CreateNewAppPlugin`.

```bash
./gradlew create-new-app -Pargs='com.example.myapp,MyNewApp' -q --console=plain
```

It excludes files such as `.git`, `.gradle`, `.idea`, `local.properties`, and `secrets.properties` so personal or local state does not leak into generated apps.

## Native package rename issue

JNI methods often depend on package-derived names. ComposeTemplate avoids fragile convention-based JNI names by using `RegisterNatives`, allowing generated package names to keep working.

## Common mistakes

- Copying local secret files.
- Leaving generator plugins inside generated apps.
- Rewriting Kotlin files but not XML or Gradle files.
- Not testing the generated app in CI.

## Production checklist

- [ ] Generated app builds from a clean checkout.
- [ ] Package name and source paths match.
- [ ] Local files are excluded.
- [ ] Template-only generator code is removed.
- [ ] Native bindings survive rename.
- [ ] CI runs create-new-app smoke tests.

## Summary

A template generator is a build tool, not a script. ComposeTemplate uses Gradle to make app generation repeatable, reviewable, and testable.