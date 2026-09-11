# Clojurust: greetings with undo, live in the REPL

An atom points to a history of immutable maps. Adding a greeting creates a new
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

Start the REPL from the example directory:

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
greetings, and live function redefinition in the actual `cljrs repl`.
