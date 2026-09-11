#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
cache=${JOLT_DEMO_CACHE:-${XDG_CACHE_HOME:-$HOME/.cache}/clojure-dialects/jolt}
rev=$(cat JOLT_REV)
export JOLT_CHEZ="$cache/chez-10.4.1/bin/scheme"
if [[ ! -x "$JOLT_CHEZ" || ! -x "$cache/jolt-$rev/bin/jolt" ]]; then
  ./setup.sh >&2
fi
exec "$cache/jolt-$rev/bin/jolt" "$@"
