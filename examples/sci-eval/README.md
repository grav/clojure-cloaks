# SCI: evaluate an expression on four hosts

One [shared `.cljc` file](src/hello/eval.cljc) passes its command-line argument to `sci/eval-string`. SCI interprets
its Clojure subset inside the host, with the host's `println` and `prn` exposed
for terminal output.

From this directory, run the same expression on each host:

```sh
clj -M src/hello/eval.cljc '(println (str "hello world"))'
bb src/hello/eval.cljc '(println (str "hello world"))'
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

The evaluation function is shared by all four hosts. Reader conditionals select
only the entry point: JVM Clojure and Babashka read `*command-line-args*`,
ClojureScript sets `*main-cli-fn*`, and ClojureDart defines `main`.
Return values are not automatically printed; use `prn` or `println`.
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
