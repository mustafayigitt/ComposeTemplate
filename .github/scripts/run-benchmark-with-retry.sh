#!/usr/bin/env bash

set -uo pipefail

readonly package_prefix="com.ytapps.composetemplate"
readonly max_attempts=3

adb wait-for-device

for attempt in $(seq 1 "$max_attempts"); do
  echo "Benchmark instrumentation attempt $attempt/$max_attempts"
  if ./gradlew :benchmark:connectedBenchmarkAndroidTest; then
    exit 0
  fi

  if (( attempt == max_attempts )); then
    break
  fi

  while IFS= read -r package_name; do
    if [[ -n "$package_name" ]]; then
      adb uninstall "$package_name" || true
    fi
  done < <(
    adb shell pm list packages 2>/dev/null |
      tr -d '\r' |
      sed 's/^package://' |
      grep "^${package_prefix}" || true
  )

  adb kill-server || true
  adb start-server
  adb wait-for-device
  sleep 20
done

echo "Benchmark instrumentation failed after $max_attempts attempts."
exit 1
