# Demo overview

Related examples are grouped together below. “Execution” describes the Clojure
code in this demo; an interpreter can itself be packaged as a native executable.
AOT means compiled ahead of execution; JIT means compiled during execution.
The JVM and CLR examples remain in the root README’s
[verification table](../README.md#implementation-status), but are left out of
this overview for now.

| Demo | Host / target | Execution | What the demo reveals |
| --- | --- | --- | --- |
| [SCI eval](sci-eval/) | JVM Clojure, Babashka, JavaScript, Dart, Chez Scheme | SCI interprets the supplied expression inside each host. | One `.cljc` evaluator runs across five compatible hosts. |
| [SCI FFI](sci-ffi/) | Rust and Zig calling a GraalVM native library | SCI is AOT-compiled into a library; expressions remain interpreted. | Embed the same evaluator through a C ABI, without a JVM process. |
| [ClojureScript + Replicant](clojurescript/) | JavaScript / browser | ClojureScript is compiled to JavaScript before loading the page. | A reactive SPA with data-driven rendering and hash routing. |
| [Scittle](scittle/) | JavaScript / browser | SCI interprets inline Clojure in the page. | Add Clojure behavior to plain HTML with script tags and an atom; no build step. |
| [Planck](planck/) | JavaScriptCore | The self-hosted ClojureScript compiler compiles expressions to JavaScript at runtime. | Evaluate ClojureScript without a JVM or Node.js. |
| [Rustly](rustly-rpi/) | Rust / bare-metal ARMv6 and ARM64 | A small Clojure subset is transpiled to Rust, then AOT-compiled to firmware. | Clojure-authored messages and Morse alphabet run on Pi 1 and Pi 4 alongside Rust hardware drivers. |
| [Clojurust](clojurust/) | Rust runtime / Cranelift | Tiered interpreter/JIT in the REPL; the AOT demo retains an interpreted entry point for `eval`. | Immutable snapshots, undo and live redefinition; a native executable can still evaluate new expressions. |
| [ClojureDart + Flutter](clojuredart/) | Dart / Flutter | Transpiled to Dart; Flutter uses JIT for native development, AOT for native release, and JavaScript for this web build. | One UI for iOS, Linux and web, with hot reload and an atom-backed scrolling greeting history. |
| [Swish + SwiftUI](swish/) | Swift / SwiftUI | A Swift-written interpreter evaluates the bundled Swish script at runtime. | Clojure data describes native controls; Swift dispatches events and explicitly refreshes the view. |
| [jank + SDL](jank/) | LLVM / native code and C++ libraries | JIT-compiles the program to native code. | Call SDL directly from jank to create a window, draw, and handle input. |
| [Glojure](glojure/) | Go | A compiled Go executable embeds the Glojure interpreter and application script. | Package a Go-hosted Clojure HTTP service in a small `FROM scratch` OCI image. |
| [Clojerl](clojerl/) | Erlang/OTP / BEAM | Compiled to BEAM bytecode; the VM executes it. | Actors on separate nodes exchange messages while each actor owns its state. |
| [Jolt](jolt/) | Chez Scheme | Compiled through Scheme using Chez's native compiler. | Ordinary tail-recursive Fibonacci runs without growing the call stack, unlike JVM Clojure. |
| [Cream](cream/) | GraalVM / Crema and Ristretto | Native executable containing Clojure's compiler; runtime-generated JVM bytecode can be JIT-compiled. | Create JVM interfaces and implementing classes at runtime inside a native executable. |

Scittle also connects naturally to the browser examples: it interprets code in
the page, while Replicant's example arrives as compiled JavaScript.
