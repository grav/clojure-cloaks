#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
log=$(mktemp)
trap 'docker compose down >/dev/null; rm -f "$log"' EXIT
docker compose up --build --abort-on-container-exit --exit-code-from client 2>&1 | tee "$log"
python3 - "$log" <<'PY'
import sys
output = open(sys.argv[1]).read()
for line in ['Hello, Ada from :greeter@greeter! Request #1',
             'Hello, world from :greeter@greeter! Request #2',
             'Two-node greeting exchange complete.']:
    assert line in output, line
print('Two-node actor greeting checks passed.')
PY
