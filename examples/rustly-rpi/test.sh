#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./toolchain.sh bash -c 'set -e; rustc --test src/morse.rs -o build/morse-tests; build/morse-tests; python3 smoke.py'
