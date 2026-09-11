#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -n ${FLUTTER_SDK:-} ]]; then
  export PATH="$FLUTTER_SDK/bin:$PATH"
elif ! command -v flutter >/dev/null 2>&1 && [[ -x ../../.cache/flutter/bin/flutter ]]; then
  export PATH="$(realpath ../../.cache/flutter)/bin:$PATH"
fi
if ! command -v flutter >/dev/null 2>&1; then
  echo 'Install Flutter or set FLUTTER_SDK; see README.md.' >&2
  exit 1
fi
if (( $# == 0 )); then
  exec flutter run -d linux
fi
exec "$@"
