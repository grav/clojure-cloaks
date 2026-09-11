# Hello, Scittle

Open [index.html](index.html) directly in your browser. Click **Say hello**:
inline Clojure updates the greeting and increments an atom. No CSS, build step,
or server. Keep `vendor/` beside the HTML file.

[Source](index.html) · [Screenshot](screenshot.png) · [Scittle documentation](https://babashka.org/scittle/)

Scittle **0.8.33** is bundled from the
[official release URL](https://cdn.jsdelivr.net/npm/scittle@0.8.33/dist/scittle.js),
with its [license](vendor/LICENSE). SHA256:
`e04a33ff90947057772f42e683314e9c9f8671ebb922398208b4ad1c76991373`.

Verified in Chromium via `file://`: initial greeting and three successive clicks.
To repeat the browser check (Node is only needed for testing):

```sh
npm ci
npx playwright install chromium
npm test
```

Set `CHROMIUM_PATH` to use an existing Chromium executable.
