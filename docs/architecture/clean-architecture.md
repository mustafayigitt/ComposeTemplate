# Clean Architecture

ComposeTemplate applies Clean Architecture through Gradle module boundaries, not only through package names.

## Dependency rule

```text
Data can know Domain.
Presentation can know Domain.
Domain should not know Data or Presentation.
```

## Layer responsibilities

- Domain: business models, repository contracts, use cases
- Data: repository implementations, Retrofit services, DTOs, mappers
- Presentation: ViewModels, UiState, events, Compose UI

## BaseRepository

`BaseRepository` should catch `IOException` and `HttpException`, but avoid catching generic `Exception` by default. Catching every exception can hide programming errors.

## Checklist

- [ ] Domain is framework-independent.
- [ ] DTOs are separate from domain models.
- [ ] UI does not use DTOs.
- [ ] Results are represented explicitly.
