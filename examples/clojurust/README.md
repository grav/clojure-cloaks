# Clojurust: runtime eval, immutable greetings, and AOT

The command-line entry point reads a Clojure expression, evaluates it, and prints
the result—even in the AOT executable. The same file also supplies a REPL demo:
an atom points to a history of immutable maps. Adding a greeting creates a new
version; undo selects the previous version. A saved snapshot keeps its value
as you edit. The application is entirely [Clojure](hello.cljrs), running in
[csm/clojurust](https://github.com/csm/clojurust)'s Rust runtime.

## Run

From this repository's root, install the pinned toolchain once:

```sh
git clone https://github.com/csm/clojurust .cache/clojurust
git -C .cache/clojurust checkout 9fad3657367efd622a5b216b00a312e84716b7df
cargo install --locked --path .cache/clojurust/crates/cljrs
```

The build requires Rust/Cargo and a native C toolchain. Ensure Cargo's install
directory (usually `~/.cargo/bin`) is on PATH. This VM already has the tested
executable installed as `~/.local/bin/cljrs`.

## Run and compile

From `examples/clojurust`, evaluate an expression through the interpreter,
or compile the evaluator ahead of time and run the resulting executable:

```sh
cljrs run hello.cljrs -- '(str "Hello, world!")'
cljrs compile hello.cljrs -o hello
./hello '(str "Hello, world!")'
```

Both runs print `"Hello, world!"`. The host uses `prn`, so strings retain quotes
and collections print as Clojure data. Supply one expression; use `do` to group
multiple operations:

```sh
./hello '(mapv inc [1 2 3])'
# => [2 3 4]
./hello '(do (hello/greet! "world") (hello/greet! "Ada") (hello/undo!))'
# => {:greetings ["Hello, world!"]}
```

The native executable includes the Clojurust runtime; running it needs neither
`cljrs`, Rust/Cargo, nor a JVM installed. It still depends on compatible OS
libraries. Compilation needs the Rust toolchain and the pinned source checkout.

Clojurust compiles the greeting functions to machine code via Cranelift. This
pinned compiler currently returns `nil` when a compiled function calls `eval`.
The example works around that by defining `-main` with `defonce`, which keeps
that small evaluator in the interpreted startup preamble. The executable still
includes everything needed to run it. The command-line expression is read and
evaluated at runtime, so you can change it without recompiling.

## REPL

Start the REPL from the repository root:

```sh
cd examples/clojurust
cljrs repl --src-path .
```

Paste these expressions one at a time:

```clojure
(require '[hello :as h])
(h/greet! "world")
;; => {:greetings ["Hello, world!"]}

(def snapshot (h/current))
(h/greet! "Ada")
;; => {:greetings ["Hello, world!" "Hello, Ada!"]}

snapshot
;; => {:greetings ["Hello, world!"]}

(h/undo!)
;; => {:greetings ["Hello, world!"]}
```

You can also redefine a function while the application is running:

```clojure
(in-ns 'hello)
(defn greeting [name] (str "Ahoy, " name "!"))
(greet! "Grace")
;; => {:greetings ["Hello, world!" "Ahoy, Grace!"]}
```

Earlier greetings retain their values; the next call uses the new function.
Enter `:quit` to exit. History lasts for this REPL session.

Persistent collections share unchanged structure between versions. The atom
provides a mutable reference to those immutable values; no deep-copy operation
is needed to save a snapshot. Keeping every version also keeps that history in
memory.

This is a Linux VM demo. Clojurust's current runtime uses Rust `std` and its AOT
launcher uses OS threads, so it cannot directly replace the Rustly bare-metal
Raspberry Pi firmware.

Verified on Linux ARM64 with Rust 1.98.0 and the pinned Clojurust revision:
snapshot preservation, undo including an empty history, duplicate and Unicode
greetings, and live function redefinition in the actual `cljrs repl`. The AOT
executable produces identical output to `cljrs run`, including when copied to a
separate directory and run with no toolchains on PATH.

Expression checks cover strings, Unicode, collections, greeting/undo calls,
`nil`, and 100,000 `loop`/`recur` iterations in both modes. Missing arguments,
malformed expressions, and unknown symbols return nonzero exit codes.
