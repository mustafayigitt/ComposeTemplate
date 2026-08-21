# Static Analysis

Static analysis turns code quality into an automated quality gate.

## Tools

- Ktlint
- Detekt

## Commands

```bash
./gradlew ktlintCheck
./gradlew detekt
```

## Checklist

- [ ] CI runs Ktlint.
- [ ] CI runs Detekt.
- [ ] New modules are covered.
- [ ] Suppressions are justified.
