#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
image=clojure-hello/rust-rpi:local
if ! docker image inspect "$image" >/dev/null 2>&1; then
  docker build -t "$image" .
fi
docker run --rm --init -v "$PWD:/app" -w /app \
  -e "DEMO_UID=$(id -u)" -e "DEMO_GID=$(id -g)" "$image" bash -c '
    "$@"
    result=$?
    for output in target build Cargo.lock; do
      if [[ -e $output ]]; then chown -R "$DEMO_UID:$DEMO_GID" "$output"; fi
    done
    exit "$result"
  ' bash "$@"
