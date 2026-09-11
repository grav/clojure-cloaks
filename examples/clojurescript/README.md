# ClojureScript + Replicant SPA

A browser app with a greeting form and a history route. An atom owns the state,
a pure function produces hiccup, and Replicant updates the DOM. Hash navigation
keeps the app and its in-memory history alive without page reloads. This uses
Replicant, not React itself.

Requires Java, Clojure CLI, and Python 3 to serve files. Node/npm is needed only
for browser tests.

```sh
cd examples/clojurescript
clojure -M:build
python3 -m http.server 8071 --bind 127.0.0.1 --directory public
```

Open http://localhost:8071, enter a name, and press **Say hello**. Visit
**Greetings** to see the history. Reloading the page intentionally resets state.
For development, run `clojure -M:dev` in another terminal and refresh after edits.

```sh
npm ci
npx playwright install chromium
npm test
```

Verified on Linux ARM64, 2026-09-11: advanced production compilation and Chromium
browser test covering greeting, blank-name fallback, literal HTML handling,
history, browser back navigation, and clearing history. No browser errors.

![Greeting app](screenshot.png)

Sources: [Replicant](https://github.com/cjohansen/replicant), versions in
[deps.edn](deps.edn); browser tooling pinned by [package-lock.json](package-lock.json).
