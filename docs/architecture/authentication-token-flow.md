# Authentication and Token Flow

Authentication is handled through clear contracts between the network layer and the auth feature.

## Main pieces

- `AuthInterceptor`
- `TokenAuthenticator`
- `ITokenRefresher`
- Auth repository
- Token/session abstractions

## Notes

`AuthInterceptor` adds access tokens to requests. `TokenAuthenticator` handles 401 responses and delegates refresh work through `ITokenRefresher`.

The template provides refresh infrastructure; the generated app must implement its real backend refresh endpoint.

## Checklist

- [ ] Token injection is handled in one place.
- [ ] 401 refresh is handled by the authenticator.
- [ ] Tokens are not logged.
- [ ] Sensitive headers are redacted.
