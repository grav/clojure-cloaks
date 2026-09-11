#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
image=clojure-hello/jank-arm64:local
if ! docker image inspect "$image" >/dev/null 2>&1; then
  docker build --platform linux/arm64 -f Dockerfile.arm64 -t "$image" .
fi
if [[ ${1:-} == --smoke ]]; then
  docker run --rm --init --platform linux/arm64 -e HELLO_SMOKE=1 \
    -v "$PWD:/app" "$image" xvfb-run -a ./run.sh
  python3 check-snapshot.py
else
  : "${DISPLAY:?Set DISPLAY for a Linux X11 desktop, or use --smoke for a virtual display}"
  display_args=(-e "DISPLAY=$DISPLAY" -v /tmp/.X11-unix:/tmp/.X11-unix:ro)
  if [[ -n ${XAUTHORITY:-} && -f $XAUTHORITY ]]; then
    display_args+=(-e XAUTHORITY=/tmp/xauthority -v "$XAUTHORITY:/tmp/xauthority:ro")
  elif [[ -f $HOME/.Xauthority ]]; then
    display_args+=(-e XAUTHORITY=/tmp/xauthority -v "$HOME/.Xauthority:/tmp/xauthority:ro")
  fi
  docker run --rm --init --platform linux/arm64 "${display_args[@]}" -v "$PWD:/app" "$image" ./run.sh
fi
