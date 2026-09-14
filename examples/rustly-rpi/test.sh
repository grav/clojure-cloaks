#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ ${1:-pi4} == pi1 ]]; then
  export RPI_BOARD=pi1
  smoke_arg=--pi1
elif [[ ${1:-pi4} == pi4 ]]; then
  smoke_arg=
else
  echo "Usage: $0 [pi4|pi1]" >&2; exit 1
fi
./toolchain.sh bash -c 'set -e; mkdir -p build; rustc --test src/morse.rs -o build/morse-tests; build/morse-tests; python3 smoke.py "$@"' bash ${smoke_arg:+"$smoke_arg"}
