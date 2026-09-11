#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
CGO_ENABLED=0 GOOS=linux go build -tags glj_aot_runtime -trimpath -o bin/hello .
docker build -t clojure-hello/glojure:local .
container=$(docker run -d --rm -p 127.0.0.1::8080 clojure-hello/glojure:local)
trap 'docker stop "$container" >/dev/null' EXIT
endpoint=$(docker port "$container" 8080/tcp)
python3 - "$endpoint" <<'PY'
import json, sys, time, urllib.request, urllib.parse
base = 'http://' + sys.argv[1]
for attempt in range(50):
    try:
        assert urllib.request.urlopen(base + '/healthz', timeout=2).read() == b'ok\n'
        break
    except OSError:
        time.sleep(.1)
else:
    raise RuntimeError('service did not become ready')
for name in ['', 'Ada', 'Ægir']:
    with urllib.request.urlopen(base + '/?' + urllib.parse.urlencode({'name': name})) as response:
        assert json.load(response) == {'language': 'Glojure', 'message': f'Hello, {name or "world"} from Glojure!'}
print('OCI health, default, named and Unicode greetings passed.')
PY
