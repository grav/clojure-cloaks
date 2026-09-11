#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ ${1:-} == --smoke ]]; then
  HELLO_SMOKE=1 xvfb-run -a ./run.sh
  python3 check-snapshot.py
else
  exec ./run.sh
fi
