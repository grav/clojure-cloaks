# Hello, Scittle

A small browser greeting written directly in an `application/x-scittle` script
tag in [index.html](index.html). No Clojure compiler, React, or application build
step is needed. [Screenshot](screenshot.png).

```sh
cd examples/scittle
python3 -m http.server 8074 --bind 0.0.0.0
```

Open http://localhost:8074 (or the VM's address on port 8074). Enter a name and
click **Say hello** or press Enter. Blank names default to “world”; input is
rendered as text, including Unicode and HTML-like strings.

Scittle 0.8.33 is bundled in `vendor/`, so the running page makes no external
requests. Runtime source:
https://cdn.jsdelivr.net/npm/scittle@0.8.33/dist/scittle.js

SHA256: `e04a33ff90947057772f42e683314e9c9f8671ebb922398208b4ad1c76991373`.
The upstream license is retained in [vendor/LICENSE](vendor/LICENSE).
See [Scittle's documentation](https://babashka.org/scittle/) and
[release v0.8.33](https://github.com/babashka/scittle/releases/tag/v0.8.33).

## Verification

Chromium on Linux ARM64 passed initial greeting, Unicode/whitespace/blank input,
literal HTML rendering, button and Enter submission, with no browser errors.
The browser test also saves `screenshot.png`.

```sh
npm ci
npx playwright install chromium
npm test
# Or use an existing Chromium installation:
CHROMIUM_PATH=/path/to/chromium npm test
```

Node and Playwright are only needed for the test. Python's static server is
sufficient to run the demo.
