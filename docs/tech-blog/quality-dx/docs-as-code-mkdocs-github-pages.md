# Docs-as-Code with MkDocs and GitHub Pages

Good developer documentation should live close to the code and evolve with it.

## Problem

Project documentation often starts in a README, grows too large, and then becomes hard to maintain. External wiki systems can drift away from the repository.

## ComposeTemplate approach

ComposeTemplate uses docs-as-code:

- Markdown files under `docs/`
- `mkdocs.yml` for navigation
- MkDocs Material for presentation
- GitHub Actions for deployment
- GitHub Pages for hosting

## Takeaway

Documentation is part of the product. Treating docs as code keeps it reviewable, versioned, and close to the engineering decisions it explains.
