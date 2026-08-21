# Extending ComposeTemplate

ComposeTemplate is designed to be extended.

## Extension areas

- Add a core module
- Add a convention plugin
- Extend `ScaffoldFeaturePlugin`
- Change `CreateNewAppPlugin` rewrite rules
- Add secret-validation rules
- Expand CI smoke tests

## Production rule

Every generator-impacting extension should be tested through the template smoke path.
