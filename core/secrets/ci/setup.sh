#!/usr/bin/env bash
set -euo pipefail

echo "STORE_FILE=debug.keystore" > local.properties
cat > secrets.properties <<'EOF'
API_KEY_DEBUG="debug_secret_value"
API_KEY_RELEASE="release_secret_value"
BASE_URL_DEBUG="https://example.com/"
BASE_URL_RELEASE="https://example.com/"
KEY_ALIAS="androiddebugkey"
KEY_PASSWORD="android"
STORE_PASSWORD="android"
XOR_MASK="ci_mask_value_with_24_chars_minimum"
EXPECTED_SIGNATURE_HASH="53017D6F6B5A861B3096D18257D5C91F0588287BC587F12C35767D362EF0A3A8"
NATIVE_RUNTIME_CHECKS_ENABLED=true
CERTIFICATE_PINNING_ENABLED=false
CERTIFICATE_PINS=""
MIN_VERSION=1
EOF
