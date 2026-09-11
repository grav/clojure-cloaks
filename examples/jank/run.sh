#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ $(uname -s) == Darwin ]]; then
  sdl=$(brew --prefix sdl2)
  exec jank -I . -I "$sdl/include" -L "$sdl/lib" -l SDL2 run hello.jank
else
  exec jank -I . -l SDL2 run hello.jank
fi
