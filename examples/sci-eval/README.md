# SCI: evaluate an expression on five hosts

One [shared `.cljc` file](src/hello/eval.cljc) passes its command-line argument to `sci/eval-string`. SCI evaluates
the expression, then the host prints its return value with `prn`.

From this directory, run the same expression on each host:

```sh
clj -M src/hello/eval.cljc '(str "hello world")'
bb src/hello/eval.cljc '(str "hello world")'
```

Jolt uses the same file with an alias selecting its tested SCI revision:

```sh
jolt -M:jolt src/hello/eval.cljc '(str "hello world")'
```

The `:jolt` alias overrides only SCI, pinning revision
`32d62a5136ad3dc148588752f5bcc4cc30b14752`. The default SCI revision needs
`clojure.core/Inst`, which our installed Jolt does not provide.

Compile the ClojureScript runner once, then run it with Node (no npm packages):

```sh
clj -M:cljs
node target/eval.js '(str "hello world")'
```

Compile the ClojureDart runner once, then run it with Dart:

```sh
clj -M:cljd compile
dart run bin/sci_eval.dart '(str "hello world")'
```

On this VM, make the existing Dart SDK available first:

```sh
export PATH="/home/grav/repo/clojure-dialects/.cache/flutter/bin:$PATH"
```

Every command prints:

```text
"hello world"
```

Reader conditionals select the entry point: JVM Clojure, Babashka and Jolt read
`*command-line-args*`, ClojureScript reads Node's `process.argv`, and ClojureDart
defines `main`. Each branch directly calls `(prn (sci/eval-string expression))`.
For example, `'(+ 1 2)'` prints `3`, and `'(map inc [1 2 3])'` prints `(2 3 4)`.
Strings print with quotes, and a `nil` result prints as `nil`.

## Compatibility

[SCI officially supports](https://github.com/babashka/sci#why) JVM Clojure,
GraalVM native images, ClojureScript/JavaScript (including advanced compilation),
and ClojureDart. Babashka bundles SCI and exposes `sci.core`; this runner uses
that built-in copy. JVM Clojure, ClojureScript and ClojureDart use the default SCI revision pinned in
`deps.edn`, including its Dart port. Their supported interop depends on the host
and the functions/classes exposed to SCI. Jolt works with its tested revision through its Clojure compatibility layer.
Other Clojure dialects are not implicitly compatible.

Verified on Linux ARM64 with Babashka 1.13.219, ClojureScript 1.12.145, Dart 3.13.3,
Jolt `0f7d1a11d951047448dcf02f0cbda48c6b74083a`,
and the pinned SCI/ClojureDart revisions: the JVM, Babashka, ClojureScript and Dart runners passed the greeting,
a closure computing `49`, a Unicode greeting, and nested evaluation of
`(eval '(+ 20 22))`, producing `42`. GraalVM is a supported upstream
platform but is not built separately in this example.

Jolt passed the greeting, arithmetic, closure, collection and nested-eval checks.
For embedding SCI from Rust or Zig through C FFI, see the [GraalVM shared-library example](../sci-ffi/).
