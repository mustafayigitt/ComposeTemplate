# Docs-as-Code with MkDocs and GitHub Pages

## Who this article is for

This article is for maintainers who want project documentation to live beside source code and evolve through normal review workflows.

## What you will learn

- why docs-as-code works well for template repositories
- how MkDocs organizes documentation
- why GitHub Pages is useful for publishing
- how documentation quality can be enforced with standards

## The problem

Documentation often drifts away from code. A wiki page explains an old module layout. A README contains newer information than the docs site. Blog posts mention classes that no longer exist.

For a template repository, stale documentation is especially harmful because downstream projects copy its assumptions.

## Why this matters for Android projects

Android templates encode architecture, build logic, security policy, CI behavior, and generation workflows. If docs are stale, developers misuse the template or cargo-cult outdated patterns.

Docs should be versioned, reviewed, and changed with the code.

## ComposeTemplate's approach

ComposeTemplate uses MkDocs with Material theme. Documentation source lives under `docs/`, and navigation is configured in `mkdocs.yml`.

The docs are organized into:

- Guides
- Architecture
- Build System
- Security
- Template Tools
- Quality
- Reference
- Tech Blog Series
- Contributing

## Documentation standard

ComposeTemplate includes a documentation standard that defines page types, required sections, quality bar, code example rules, repository reference rules, and accuracy expectations.

This prevents docs from becoming shallow checklists.

## Publishing workflow

GitHub Pages can publish the MkDocs site from CI. This keeps docs public, searchable, and tied to repository history.

## Design trade-offs

Docs-as-code requires contributors to edit Markdown and submit PRs. It is less freeform than a wiki, but much more reviewable.

For a production template, reviewability is more important than informal editing.

## Production checklist

- [ ] docs live in the repository
- [ ] navigation is defined in `mkdocs.yml`
- [ ] page types have a clear standard
- [ ] code examples match real repository files
- [ ] docs changes are reviewed through PRs
- [ ] generated docs are published consistently

## Takeaways

- Documentation is part of the product surface of a template.
- Docs-as-code keeps architecture decisions close to implementation.
- A documentation standard prevents shallow pages from spreading.
