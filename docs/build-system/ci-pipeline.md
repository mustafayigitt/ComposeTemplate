# CI Pipeline

The CI pipeline validates both the current app and template generator behavior.

## Jobs

1. Lint
2. Unit tests
3. Debug + release build
4. Template smoke test

## Template smoke test

Validates feature scaffolding, database feature scaffolding, generated feature compilation, create-new-app execution, and exclusion of local secrets from generated apps.

## Checklist

- [ ] Lint job exists.
- [ ] Unit-test job exists.
- [ ] Debug/release build job exists.
- [ ] Scaffold smoke test exists.
- [ ] Create-new-app smoke test exists.
