#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
project=$PWD
sdk=$(realpath "${FLUTTER_SDK:-../../.cache/flutter}")
pub_cache=${PUB_CACHE:-$HOME/.pub-cache}
mkdir -p "$pub_cache"
image=clojure-hello/flutter-linux-deps:local
if ! docker image inspect "$image" >/dev/null 2>&1; then
  docker build -f Dockerfile.linux -t "$image" .
fi
docker run --rm --init \
  -v "$project:$project" -v "$sdk:$sdk" -v "$pub_cache:$pub_cache" \
  -e "FLUTTER_SDK=$sdk" -e "PUB_CACHE=$pub_cache" \
  -e "DEMO_UID=$(id -u)" -e "DEMO_GID=$(id -g)" -w "$project" \
  "$image" bash -c '
    git config --global --add safe.directory "$FLUTTER_SDK"
    export PATH="$FLUTTER_SDK/bin:$PATH"
    "$@"
    result=$?
    for output in build .dart_tool linux/flutter/ephemeral; do
      if [[ -e $output ]]; then chown -R "$DEMO_UID:$DEMO_GID" "$output"; fi
    done
    exit "$result"
  ' bash "$@"
