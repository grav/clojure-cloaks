# Planck: evaluate ClojureScript at runtime

[Planck](https://planck-repl.org/) runs the self-hosted ClojureScript compiler
on JavaScriptCore, Apple's JavaScript engine. This script reads an expression
from its argument and calls `eval`, compiling and executing it at runtime.
Planck and JavaScriptCore also run on Linux; this example uses no macOS-specific
APIs. See Planck's [installation guide](https://planck-repl.org/guide-all.html)
for Linux installation options.

Install on macOS:

```sh
brew install planck
```

Run:

```sh
cd examples/planck
cat eval.cljs
planck eval.cljs '(println (str "hello world"))'
```

Output:

```text
hello world
```

The entire script is:

```clojure
(require '[planck.core :refer [eval read-string]])

(eval (read-string (first *command-line-args*)))
```

Pass one ClojureScript form; use `do` to group several expressions. The script
evaluates the form without printing its return value, so use `println` or `prn`
when you want output. No JVM, Node, dependencies, or separate build step is needed.

Verified with Planck 2.28.0 on macOS ARM64: the greeting above, an anonymous
function computing `49`, and a Unicode greeting all produced the expected output
with exit status zero.
