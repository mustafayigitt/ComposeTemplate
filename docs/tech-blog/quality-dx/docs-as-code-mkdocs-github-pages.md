# Docs-as-Code with MkDocs and GitHub Pages

## Who this article is for

This article is for open-source maintainers and engineering teams that want project documentation to evolve with code.

## What you will learn

- Why README-only documentation does not scale
- What docs-as-code means
- Why MkDocs Material works well for developer docs
- How GitHub Pages deployment fits the workflow

## The problem

A README is a good entry point, but it becomes overloaded as a project grows. Architecture, security, build system, performance, and contribution docs need structure.

External wiki systems can drift because changes are not reviewed with code.

## Docs-as-code mental model

Documentation lives in the repository as Markdown. Changes go through pull requests, review, CI, and version history.

This makes documentation part of the engineering workflow.

## ComposeTemplate approach

ComposeTemplate uses:

- `docs/` for Markdown pages,
- `mkdocs.yml` for navigation,
- MkDocs Material for site rendering,
- GitHub Actions for deployment,
- GitHub Pages for hosting.

## Why MkDocs Material

MkDocs Material provides navigation, search, code highlighting, admonitions, and a polished reading experience while keeping content in Markdown.

## CI and deployment

The Pages workflow builds the site and deploys it. This means broken navigation or invalid docs configuration can be caught before publishing.

## Common mistakes

- Keeping important docs only in README.
- Publishing docs that are not reviewed.
- Letting navigation drift from file structure.
- Not testing documentation builds.

## Production checklist

- [ ] Docs live in the repository.
- [ ] Navigation is defined in `mkdocs.yml`.
- [ ] Docs build in CI.
- [ ] README links to the documentation site.
- [ ] Architecture and security docs are reviewed like code.

## Summary

Documentation is part of developer experience. ComposeTemplate uses docs-as-code so documentation remains versioned, reviewable, and close to implementation decisions.