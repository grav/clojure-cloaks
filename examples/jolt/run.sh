#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
exec ./jolt.sh demo.clj "$@"
