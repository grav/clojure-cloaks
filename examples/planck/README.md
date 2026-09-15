# Planck: evaluate ClojureScript at runtime

[Planck](https://planck-repl.org/) runs the self-hosted ClojureScript compiler
on JavaScriptCore, Apple's JavaScript engine. This script reads an expression
from its argument and calls `eval`, compiling and executing it at runtime.
Planck and JavaScriptCore also run on Linux; this example uses no macOS-specific
APIs.

[Lumo](https://github.com/anmonteiro/lumo) offers a similar self-hosted
ClojureScript environment on Node.js and its V8 engine, with access to Node.js
APIs, instead of Planck's JavaScriptCore host.

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

Verified with Planck 2.28.0 on macOS ARM64 and this Arch Linux ARM64 VM
(JavaScriptCore GTK 4.1, version 2.52.6): the greeting above, an anonymous
function computing `49`, and a Unicode greeting all produced the expected output
with exit status zero.

## Installation

See Planck's [installation guide](https://planck-repl.org/guide-all.html)
for Linux installation options.

Install on macOS:

```sh
brew install planck
```

On this Arch Linux ARM64 VM, Planck is installed in `~/.local/bin`.
To reproduce the native source build (Java and the Clojure CLI are needed only
to build Planck):

```sh
sudo pacman -S --needed base-devel cmake curl zlib libzip icu webkit2gtk-4.1 tinyxxd
git clone --depth 1 --branch 2.28.0 https://github.com/planck-repl/planck.git
cd planck
sed -i 's/javascriptcoregtk-4.0/javascriptcoregtk-4.1/' planck-c/CMakeLists.txt
cmake -S planck-c -B planck-c/build -DCMAKE_POLICY_VERSION_MINIMUM=3.5 -DCMAKE_C_STANDARD=17
script/build
script/install -p "$HOME/.local"
```

The package-name adjustment selects Arch's JavaScriptCore GTK 4.1 library;
the CMake options accommodate modern CMake and select C17 for the older C source.
Ensure `~/.local/bin` is on your `PATH`, then run the example from this repository.
