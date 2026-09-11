# SCI: evaluate an expression on four hosts

Each runner passes its command-line argument to `sci/eval-string`. SCI interprets
its Clojure subset inside the host, with the host's `println` and `prn` exposed
for terminal output. This is ordinary embedding; the separate
[self-hosting experiment](../sci-self-host/) interprets SCI's implementation.

From this directory, run the same expression on each host:

```sh
clj -M eval.clj '(println (str "hello world"))'
bb eval.clj '(println (str "hello world"))'
```

Compile the ClojureScript runner once, then run it with Node (no npm packages):

```sh
clj -M:cljs
node target/eval.js '(println (str "hello world"))'
```

Compile the ClojureDart runner once, then run it with Dart:

```sh
clj -M:cljd compile
dart run bin/sci_eval.dart '(println (str "hello world"))'
```

On this VM, make the existing Dart SDK available first:

```sh
export PATH="/home/grav/repo/clojure-dialects/.cache/flutter/bin:$PATH"
```

Every command prints:

```text
hello world
```

The JVM and Babashka runner is just:

```clojure
(require '[sci.core :as sci])

(sci/eval-string (first *command-line-args*)
                 {:bindings {'println println 'prn prn}})
```

The other runners use the same call, with their platform's command-line entry
point. Return values are not automatically printed; use `prn` or `println`.
For example, `'(prn (map inc [1 2 3]))'` prints `(2 3 4)`.

## Compatibility

[SCI officially supports](https://github.com/babashka/sci#why) JVM Clojure,
GraalVM native images, ClojureScript/JavaScript (including advanced compilation),
and ClojureDart. Babashka bundles SCI and exposes `sci.core`; this runner uses
that built-in copy. The other three hosts use the SCI source revision pinned in
`deps.edn`, including its Dart port. Their supported interop depends on the host
and the functions/classes exposed to SCI. Other Clojure dialects are not
implicitly compatible.

Verified on Linux ARM64 with Babashka 1.13.219, ClojureScript 1.12.145, Dart 3.13.3,
and the pinned SCI/ClojureDart revisions: all four runners passed the greeting,
a closure computing `49`, a Unicode greeting, and nested evaluation of
`(println (eval '(+ 20 22)))`, producing `42`. GraalVM is a supported upstream
platform but is not built separately in this example.
