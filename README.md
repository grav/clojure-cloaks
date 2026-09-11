# Clojure Cloaks

Platform-idiomatic hello-world demos across Clojure dialects, runtimes, and related languages.

Research snapshot: **11 September 2026**. This repository will demonstrate some form of hello world across the Clojure family. The initial research inventories the targets; implementation progress and verification are tracked below.

The catalogue contains **134 projects and historical attempts**. It accounts for every entry in the 111-project Clojure-like index, reconciles three other community catalogues, and adds projects found through primary sources and historical searches. **134 is not a count of distinct Clojure dialects.**

| Category | Count | Treatment for hello world |
| --- | ---: | --- |
| A — Dialects, implementations and compilers | 63 | Main implementation backlog, including partial and historical ports |
| B — Runtimes, environments and alternate syntax | 17 | Additional ways to execute Clojure-family code |
| C — Related languages and educational implementations | 46 | Extended coverage; explicitly distinguish from Clojure compatibility |
| D — Tools and syntax layers | 4 | Retain as exclusions or optional demonstrations |
| E — Proposals and attempts without an established implementation | 4 | Preserve the lead; do not promise a runnable example |

**Working target: 80 entries in A + B**, plus the related-language appendix if “all dialects” is intended in the broad community sense. Some of those 80 are unfinished and may only support a reader/runtime demonstration, or may require a documented blocker. Category membership is a research judgment, not an upstream certification of compatibility.

No finite web search can prove that every private, deleted, unindexed, unnamed or newly published interpreter has been found. This is a comprehensive, auditable public inventory with an explicit boundary, rather than a claim of mathematical completeness. The [research notes](research/methodology.md) explain coverage, ambiguities and remaining gaps. [Machine-readable catalogue](research/catalogue.json) contains the same entries, source URLs, selected repository metadata and documentation hashes.


## Implementation status

Updated as each demo is verified. “Working” means the listed execution checks passed; it does not imply full language conformance.

| Demo | Status | Verification |
| --- | --- | --- |
| Cream / runtime JVM types | [Native compiler demo](examples/cream/) | Linux ARM64: native Cream creates an interface and implementing class at runtime; default, named, Unicode, quoted-name and eval checks pass. |
| Planck / runtime compilation | [Eval demo](examples/planck/) | Planck 2.28.0, native Linux and macOS ARM64 / JavaScriptCore: command-line expressions compiled and evaluated; greeting, anonymous function and Unicode output verified. |
| ClojureScript + Replicant SPA | [Working demo](examples/clojurescript/) | Opens directly via file:// after compilation; Chromium verifies form, hash routes, browser back, clear, blank and HTML-like input; Linux ARM64. |
| Glojure OCI service | [Working demo](examples/glojure/) | Local Go build; packaging-only scratch OCI image with size inspection instructions; health, named/default and Unicode requests under a non-root container; Linux ARM64. |
| Clojerl two-node actors | [Working demo](examples/clojerl/) | Two separate BEAM nodes exchanged correlated greeting replies with actor-owned request counts; OTP 26, Linux ARM64. |
| jank native graphics | [Working demo](examples/jank/) | Native macOS and Linux ARM64 SDL windows; all SDL handling is in jank with no C++ wrapper; Linux runs directly with a local LLVM/GCC bundle; synthetic Space key and captured pixels verified. Linux ARM64 source build also passes jank JIT/AOT health checks; x86-64 emulation remains an optional fallback. |
| Swish + SwiftUI | [Working demo](examples/swish/) | Swish defines the screen, atom-backed state and events; a SwiftUI host renders native controls. macOS tests and iOS 26.5 simulator launch/screenshot verified. |
| Clojure / JVM HTTP + REPL | [Working demo](examples/jvm/) | Linux ARM64: HTTP greetings, Java timestamp, socket REPL redefinition and preserved atom counter; nine integration assertions passed. |
| ClojureCLR / C# interop | [Working demo](examples/clr/) | Native Linux ARM64, .NET SDK 8.0.425 (no container): C# calls ClojureCLR; .NET formatting and UTC date APIs; default, Unicode and quoted-input checks passed. |
| SCI / C FFI + GraalVM | [Native library demo](examples/sci-ffi/) | Linux ARM64: Rust and Zig call GraalVM-compiled SCI through C FFI; expressions and relocation verified without a JVM process. |
| SCI / expression evaluation | [Five-host eval demo](examples/sci-eval/) | Linux ARM64: JVM Clojure, Babashka, compiled ClojureScript on Node, ClojureDart on Dart, and Jolt with its compatible SCI pin; expression evaluation verified. |
| ClojureDart + Flutter | [Demo + screenshots](examples/clojuredart/) | Web release + Chromium, native Linux ARM64 + GTK (host build/run, no Docker), and iOS 26.5 simulator: Unicode/blank greetings and counter integration checks passed. Newest-first scrollable history additionally checked on Linux and web. |
| Scittle / browser script tags | [Demo + screenshot](examples/scittle/) | Plain HTML with inline Clojure and an atom; Chromium verified file:// loading and successive button-click greetings. No CSS, server, or build step. |
| Jolt / tail-call Fibonacci | [Native demo + comparison output](examples/jolt/) | Linux ARM64, Chez 10.4.1: ordinary tail recursion computes Fibonacci(20000), checked against an independent reference; identical function overflows on JVM Clojure with default stack settings. No Docker required. |
| Rustly / Raspberry Pi 4 firmware | [Hardware hello + Morse demo](examples/rustly-rpi/) | Original Clojure-to-Rust hello passed physical Pi 4 boot and Unicode echo. Serial-controlled Morse extension passes sequencer and QEMU tests; Morse image passed physical Pi 4 boot and accepted `clj` over serial; visual LED verification pending. |

The [pure Rust Raspberry Pi boot and UART baseline](research/rust-rpi/) is kept
in research as supporting hardware work for the Rustly example.

## Recent projects worth checking first

Repository creation dates below were checked against project-host metadata. They are **not necessarily invention or first-release dates**. Projects may predate their repository, and forks can have much older creation dates than their public announcement.

| Repository created | Project | Host / target |
| --- | --- | --- |
| 2026-01-01 | [ClojureElisp](https://github.com/BuddhiLW/clojure-elisp) | Emacs Lisp |
| 2026-01-18 | [FOL](https://github.com/frankadrian314159/fol) | Common Lisp / SBCL |
| 2026-02-01 | [ClojureWasm](https://github.com/clojurewasm/ClojureWasm) | Zig / native; Wasm FFI |
| 2026-02-10 | [Swish](https://github.com/infiniteNIL/swish) | Swift |
| 2026-02-22 | [Cljam](https://github.com/RegiByte/cljam) | JavaScript / TypeScript / Node.js / Bun |
| 2026-02-22 | [Cream](https://github.com/borkdude/cream) | GraalVM native-image / Crema |
| 2026-03-02 | [clojurust (csm)](https://github.com/csm/clojurust) | Rust / Cranelift |
| 2026-03-03 | [Kiso](https://github.com/clojurewasm/Kiso) | JavaScript / TypeScript compiler |
| 2026-03-10 | [ClojureFnl](https://gitlab.com/andreyorst/clojurefnl) | Fennel / Lua |
| 2026-04-13 | [mino](https://github.com/leifericf/mino) | ANSI C |
| 2026-04-18 | [cljrs](https://github.com/tytrdev/cljrs) | Rust / MLIR / LLVM / GPU path |
| 2026-04-24 | [clojure-py (2026)](https://github.com/clojure-py/clojure-py) | Python 3.14 / Cython |
| 2026-05-08 | [Bara Lang](https://gitlab.com/balvatar/lisp-nim) | Nim |
| 2026-06-01 | [Jolt](https://github.com/jolt-lang/jolt) | Scheme / Chez; Gambit for browser |
| 2026-07-11 | [cljgo](https://github.com/muthuishere/cljgo) | Go |
| 2026-07-21 | [Cljbang](https://github.com/borkdude/cljbang.el) | Emacs Lisp |
| 2026-08-08 | [Choq](https://github.com/squint-cljs/choq) | QuickJS / Cherry |

There is direct evidence of LLM-assisted language development: [Swish](https://github.com/infiniteNIL/swish) describes starting as a Claude Code experiment, and [Casey Marshall's clojurust development log](https://log.clj.rs/) discusses using coding agents. These examples support the observation that new implementations appeared in the agent era; they do not establish that all recent dialects were AI-generated.

A few easy-to-miss corrections:

- [go-joker](https://github.com/rcarmo/go-joker) is discussed as a 2026 development in community catalogues, but its fork repository was created in 2019. Treat it as renewed fork development, not a newly created repository.
- [Glojure](https://github.com/glojurelang/glojure) was first released in 2023 according to the community history, while its repository dates to November 2022. [jank](https://compiler-research.org/blogs/jank_intro/) and [Torque](https://github.com/torque-project/torque) also began well before the recent LLM era.
- [ClojureWasm](https://github.com/clojurewasm/ClojureWasm) now explicitly says it is unmaintained. [Kiso](https://github.com/clojurewasm/Kiso) is archived. [Cljam](https://github.com/RegiByte/cljam) says it reached its design goal and is not actively maintained. A recent push alone does not establish maintenance.

## A — Dialects, implementations and compilers

Includes the reference implementation, ports, ClojureScript backends, partial implementations and explicitly self-described dialects. These are **63 implementation/compiler projects**, not 63 independently designed languages. All buildability and compatibility claims remain untested here.

| Project / source | Host or target | Classification and hello-world implications |
| --- | --- | --- |
| [Bara Lang](https://gitlab.com/balvatar/lisp-nim) | Nim | Self-described Clojure for Nim; GitLab repository archived, Codeberg continuation needs checking. |
| [Basilisp](https://github.com/basilisp-lang/basilisp) | Python 3 | Clojure-compatible dialect with Python interop. |
| [Calcit](https://github.com/calcit-lang/calcit) | Rust / JavaScript | Self-described ClojureScript dialect with indentation-based Cirru syntax; now has extensive typed features. |
| [Cherry](https://github.com/squint-cljs/cherry) | JavaScript ES modules | Alternative ClojureScript compiler with persistent CLJS data structures. |
| [CLClojure](https://github.com/joinr/clclojure) | Common Lisp | Experimental Clojure port, distinct from Cloture. |
| [Cljam](https://github.com/RegiByte/cljam) | JavaScript / TypeScript / Node.js / Bun | Clojure interpreter with tree-walker and VM; author says complete and not actively maintained. |
| [Cljbang](https://github.com/borkdude/cljbang.el) | Emacs Lisp | clj!: compiles and evaluates Clojure forms in Emacs. |
| [cljgo](https://github.com/muthuishere/cljgo) | Go | muthuishere/cljgo: compiler emitting Go plus interpreted REPL. |
| [cljrs](https://github.com/tytrdev/cljrs) | Rust / MLIR / LLVM / GPU path | tytrdev/cljrs: independent implementation, not ClojureRS or csm/clojurust. |
| [cljs2go](https://github.com/hraberg/cljs2go) | Go | Alternative ClojureScript backend implemented as an overlay; old CLJS baseline. |
| [Cloje](https://gitlab.com/cloje/cloje) | CHICKEN / Racket | Clojure clone layered on Scheme; small subset, documented CHICKEN and Racket support. |
| [Clojerl](https://github.com/clojerl/clojerl) | BEAM / Erlang | Clojure implementation for the Erlang VM. |
| [Clojure](https://github.com/clojure/clojure) | JVM | Reference implementation; CLI or Java entry point. |
| [clojure-clr-next](https://github.com/dmiller/clojure-clr-next) | .NET / F# | New ClojureCLR implementation under development; keep distinct from released ClojureCLR. |
| [clojure-objc](https://github.com/galdolber/clojure-objc) | Objective-C | Clojure compiler targeting Objective-C runtimes; historical toolchain. |
| [clojure-py (2026)](https://github.com/clojure-py/clojure-py) | Python 3.14 / Cython | New clojure-py implementation attempt; package manifest differs from repository's PyO3 description; entry point unverified. |
| [clojure-py (historical; drewr fork)](https://github.com/drewr/clojure-py) | Python | Historical clojure-py, preserved in drewr fork; original halgari link unavailable. |
| [clojure-rt](https://github.com/mll/clojure-rt) | C++ / LLVM / C runtime | Clojure Real Time compiler; README describes bootstrap phase. |
| [clojure-scheme](https://github.com/takeoutweight/clojure-scheme) | Gambit Scheme / C | ClojureScript-derived Scheme backend; explicitly unmaintained. |
| [ClojureC](https://github.com/schani/clojurec) | C / native | ClojureScript-derived C compiler; explicitly no longer actively maintained. |
| [ClojureCLR](https://github.com/clojure/clojure-clr) | .NET / CLR | Official Clojure implementation on the CLR. |
| [ClojureDart](https://github.com/Tensegritics/ClojureDart) | Dart / Flutter | Clojure dialect compiling to Dart; console hello need not use Flutter. |
| [ClojureElisp](https://github.com/BuddhiLW/clojure-elisp) | Emacs Lisp | ClojureElisp: separate compiler from Cljbang; .cljel source files. |
| [ClojureFnl](https://gitlab.com/andreyorst/clojurefnl) | Fennel / Lua | Clojure-to-Fennel proof of concept targeting Lua; .cljf sources; semantics-preserving goals with incomplete compatibility. |
| [ClojureHaxe](https://github.com/ClojureHaxe/ClojureHaxe) | Haxe / multiple targets | Experimental Clojure port; README says compiler/core loading work remains. |
| [ClojureRS](https://github.com/clojure-rs/ClojureRS) | Rust | clojure-rs/ClojureRS: interpreter project; distinct from other Rust ports. |
| [ClojureRust (chimez)](https://github.com/chimez/clojure-rust) | Rust | chimez/clojure-rust: Clojure-to-Rust source compiler. |
| [ClojureScript](https://github.com/clojure/clojurescript) | JavaScript | Official Clojure-to-JavaScript compiler; browser and server targets. |
| [Clojurescript-Lua](https://github.com/raph-amiard/clojurescript-lua) | Lua | ClojureScript compiler with Lua backend; alpha. |
| [ClojureScript-Terra](https://github.com/ohpauleez/cljs-terra) | Terra / Lua / LLVM | Experimental ClojureScript backend; earlier design name CLIC. |
| [ClojureWasm](https://github.com/clojurewasm/ClojureWasm) | Zig / native; Wasm FFI | Independent Clojure runtime; current README explicitly says no longer maintained. |
| [ClojuRS (naomijub)](https://github.com/naomijub/ClojuRS) | Rust | naomijub/ClojuRS: separate Clojure experiment; sparse documentation. |
| [ClojuRust (clojurust)](https://github.com/clojurust/clojurust) | Rust | clojurust/clojurust: separate proof-of-concept implementation. |
| [clojurust (csm)](https://github.com/csm/clojurust) | Rust / Cranelift | csm/clojurust: Rust-hosted Clojure dialect with JIT and AOT. |
| [Cloture](https://github.com/ruricolist/cloture) | Common Lisp | Clojure implementation interoperating with Common Lisp; pre-alpha. |
| [Cormorant](https://github.com/austinzheng/Cormorant) | Swift | Clojure dialect interpreter; formerly Lambdatron / Swift Lambdatron. |
| [Ferret](https://github.com/nakkaya/ferret) | C++ / embedded native | Clojure-family embedded language compiling to standalone C++. |
| [FOL](https://github.com/frankadrian314159/fol) | Common Lisp / SBCL | Self-described Clojure dialect with CLOS-style objects and array programming; substantially extended semantics. |
| [Glojure (glojurelang)](https://github.com/glojurelang/glojure) | Go | glojurelang/Glojure: hosted Clojure with Go interop. |
| [Glojure (venantius)](https://github.com/venantius/glojure) | Go | venantius/Glojure: separate historical compiler; explicitly incomplete. |
| [go-joker](https://github.com/rcarmo/go-joker) | Go | rcarmo/go-joker: independently developed fork of Joker for an agent runtime. |
| [Gojure](https://github.com/tcard/gojure) | Go | tcard/gojure: historical Clojure-to-Go compiler. |
| [Hammock](https://github.com/jgdavey/hammock) | Ruby | Partial pure-Ruby Clojure implementation; proof of concept. |
| [jank](https://github.com/jank-lang/jank) | C++ / LLVM | Native Clojure dialect with C++ interop. |
| [JO Clojure](https://github.com/Zelex/jo_clojure) | C / C++ | JO Clojure: Clojure-like implementation intended for embedding. |
| [Joker](https://github.com/candid82/joker) | Go | Clojure subset interpreter with linter and formatter; documented CLI. |
| [Jolt](https://github.com/jolt-lang/jolt) | Scheme / Chez; Gambit for browser | Self-hosted Clojure implementation; current host is Scheme, not its original Janet host. |
| [Kalai](https://github.com/kalai-transpiler/kalai) | Rust / Java / C++ | Restricted Clojure source-to-source compiler; backend-specific build required. |
| [Kiso](https://github.com/clojurewasm/Kiso) | JavaScript / TypeScript compiler | Independent ClojureScript-to-JavaScript compiler. GitHub repository is archived. |
| [Las3r](https://github.com/aemoncannon/las3r) | Flash / AVM2 / ActionScript 3 | Clojure-based Lisp compiler for AVM2; historical Flash environment required. |
| [let-go](https://github.com/nooga/let-go) | Go / bytecode VM | Clojure dialect with interpreter, compiler and Go interop. |
| [Lingy](https://github.com/lingy-lang/lingy) | Perl | Clojure implementation for Perl; other host directories represent porting work, not verified runtimes. |
| [Lokke](https://github.com/lokke-org/lokke) | Guile Scheme | Experimental Clojure dialect for Guile. |
| [mino](https://github.com/leifericf/mino) | ANSI C | Embeddable Lisp participating in Clojure conformance comparison; include as partial implementation. |
| [nanoclj](https://github.com/rekola/nanoclj) | C | Tiny Clojure interpreter intended for embedding. |
| [Rouge](https://github.com/vic/rouge) | Ruby | Rouge: Clojure-based Ruby Lisp; historical source-run REPL. |
| [Rustly](https://github.com/timothypratley/rustly) | Rust | Clojure-to-Rust transpiler; hello-world build example documented. |
| [SCI](https://github.com/babashka/sci) | Clojure / ClojureScript hosts | Small Clojure Interpreter; library embedding is a valid hello-world route. |
| [Squint](https://github.com/squint-cljs/squint) | JavaScript | ClojureScript dialect using JavaScript data structures. |
| [Swish](https://github.com/infiniteNIL/swish) | Swift | Embeddable/compiler implementation; author now describes it as full-blown Clojure, started with Claude Code. |
| [TimL](https://github.com/tpope/timl) | VimL / Vim | Clojure-like API and compiler for Vim; editor-hosted example. |
| [Torque](https://github.com/torque-project/torque) | LLVM / native | Torque compiler; documentation provides Docker build and native REPL. |
| [Zera-5](https://github.com/delonnewman/zera-5) | JavaScript | Experimental lightweight Clojure implementation. |

## B — Runtimes, environments and alternate syntax

These are useful separate hello-world targets even where they share a language or interpreter. For example, SCI, Babashka, nbb, Scittle, Joyride and obb are related; a browser script and a VS Code script still require different demonstrations. Gloat and its temporary Glojure fork are implementation variants, not two additional language designs.

| Project / source | Host or target | Classification and hello-world implications |
| --- | --- | --- |
| [Arcadia](https://github.com/arcadia-unity/Arcadia) | Unity / ClojureCLR | Unity integration; formerly clojure-unity. |
| [Babashka](https://github.com/babashka/babashka) | Native / SCI | Native scripting interpreter built with GraalVM; does not require a JVM at execution time. |
| [Choq](https://github.com/squint-cljs/choq) | QuickJS / Cherry | Choq packages Cherry inside QuickJS; experimental standalone runtime. |
| [Closh](https://github.com/dundalek/closh) | Clojure / ClojureScript shell | Closh shell environment; author declared hiatus in 2022. |
| [Cream](https://github.com/borkdude/cream) | GraalVM native-image / Crema | Experimental runtime for full JVM Clojure using a custom Clojure fork. |
| [Esprit](https://github.com/mfikes/esprit) | ESP32 / Espruino | ClojureScript runtime support for embedded hardware. |
| [Gloat](https://github.com/gloathub/gloat) | Go / Glojure | Glojure Automation Tool; compile/run Clojure or YAMLScript using Glojure. |
| [Glojure (Gloat fork)](https://github.com/gloathub/glojure) | Go / Glojure fork | Temporary implementation fork supporting Gloat; same language family as upstream Glojure. |
| [Goby](https://github.com/mfikes/goby) | iOS / JavaScriptCore | ClojureScript and Objective-C integration, not a new language. |
| [Joyride](https://github.com/BetterThanTomorrow/joyride) | VS Code / SCI | ClojureScript scripting environment with access to the VS Code API. |
| [Lumo](https://github.com/anmonteiro/lumo) | Node.js / V8 | Standalone self-hosted ClojureScript environment; older toolchain. |
| [nbb](https://github.com/babashka/nbb) | Node.js / SCI | ClojureScript scripting runtime using SCI. |
| [obb](https://github.com/babashka/obb) | macOS / OSA / SCI | ClojureScript automation of Mac applications via osascript. |
| [Planck](https://github.com/planck-repl/planck) | JavaScriptCore | Standalone ClojureScript REPL and scripting runtime. |
| [Scittle](https://github.com/babashka/scittle) | Browser / SCI | SCI exposed through script tags. |
| [uclj](https://github.com/erdos/uclj) | GraalVM native-image | Alternative native Clojure interpreter using JVM Clojure runtime components. |
| [YAMLScript](https://github.com/yaml/yamlscript) | Clojure / SCI; Glojure via Gloat | YS / YAMLScript: alternate YAML syntax compiling to Clojure; count host bindings as integrations. |

## C — Related languages and educational implementations

These projects belong in a broad Clojure-family survey. Inclusion here does **not** mean they run ordinary Clojure programs. Phel, Pixie and other close languages are classified inconsistently across community lists; Phel is retained here because its own project presents an independent PHP language. MAL and malc are one language with different execution implementations.

| Project / source | Host or target | Classification and hello-world implications |
| --- | --- | --- |
| [Apricot](https://github.com/apricot-lang/apricot) | Ruby / Rubinius | Independent Clojure-inspired Lisp compiled to Rubinius bytecode. |
| [Bars](https://github.com/katehonz/bars-lang) | Native / Cranelift / LLVM | Independent systems language with Clojure syntax and ownership semantics. |
| [Beagle](https://github.com/tompassarelli/beagle) | Clojure / JavaScript / Nix; native experiments | Author explicitly calls it an independent statically typed Lisp derived from Clojure. |
| [Bridje](https://github.com/bridje/bridje) | Graal JVM | Independent statically typed language; current design includes C/Java-style surface syntax. |
| [Carbonate](https://github.com/7even/carbonate) | Ruby | Independent Lisp transpiling to Ruby, heavily influenced by Clojure. |
| [Carp](https://github.com/carp-lang/Carp) | C / native | Independent statically typed Lisp with ownership tracking. |
| [Charon](https://github.com/sigmasoldi3r/charon-lang) | Lua | Independent functional Lisp transpiler; alpha. |
| [CljPerl](https://github.com/wehu/CljPerl) | Perl | CljPerl: Clojure-inspired Lisp with Perl and CPAN interop. |
| [Clojette](https://github.com/lattiahirvio/Clojette) | GreyScript / MiniScript / GreyHack | Independent Clojure-like language; requires game environment for intended use. |
| [Closhure](https://github.com/kimtg/Closhure) | .NET | Embedded Lisp with Clojure-like syntax; distinct from ClojureCLR and Clarp. |
| [Convex Lisp](https://github.com/Convex-Dev/convex) | Convex VM | Clojure-related smart-contract language; separate runtime and semantics. |
| [Crisp](https://github.com/rhysd/Crisp) | Crystal | Independent Lisp based on MAL. |
| [Fennel](https://github.com/bakpakin/Fennel) | Lua | Independent Lua Lisp; distinct from ClojureFnl. |
| [Fleck](https://github.com/chr15m/flk) | Bash / MAL lineage | Clojure-like Lisp for Bash; author no longer actively developing it. |
| [Frock](https://github.com/chr15m/frock) | PHP | Clojure-flavoured PHP transpiler; separate from Phel. |
| [gherkin](https://github.com/alandipert/gherkin) | Bash 4 | Independent functional Lisp; explicitly dormant. |
| [Glisp](https://github.com/baku89/glisp) | Browser / TypeScript | Custom Lisp inside a graphical design environment. |
| [HC-Lisp](https://github.com/HectorIFC/hc-lisp) | TypeScript / JavaScript | Educational Clojure/Norvig-inspired Lisp; explicitly experimental. |
| [Hy](https://github.com/hylang/hy) | Python | Independent Lisp embedded in Python; Python AST semantics. |
| [Janet](https://github.com/janet-lang/janet) | C / bytecode VM | Independent scripting and embeddable Lisp. |
| [Javelin](https://github.com/kimtg/Javelin) | JVM | kimtg/Javelin: embedded Lisp with Clojure-like syntax; not Hoplon's Javelin library. |
| [Joxa](https://github.com/joxa/joxa) | BEAM / Erlang | Independent Lisp; README explicitly distinguishes it from Clojure. |
| [Kapok](https://github.com/kapok-lang/kapok) | BEAM / Erlang | Independent Lisp with Clojure-like syntax. |
| [ki](https://github.com/lantiga/ki) | JavaScript / sweet.js | Lisp with Clojure-like semantics and Mori collections embedded in JavaScript. |
| [Lemma](https://github.com/baguette/lemma) | Lua | Independent Lisp influenced by Scheme, Arc and Clojure. |
| [Lispery](https://gitlab.com/ollb/lispery) | .NET | Embedded Lisp influenced by Schemy and Clojure; project description verified. |
| [llr](https://github.com/dirkschumacher/llr) | R | Small Clojure-inspired Lisp implemented in and compiling to R. |
| [Lux](https://github.com/LuxLang/lux) | JVM and other backends | Independent statically typed language; Clojure is a syntax influence. |
| [Magic](https://github.com/mikera/magic) | JVM | Independent experimental typed Lisp influenced by Clojure. |
| [mal](https://github.com/kanaka/mal) | Many hosts | Educational Make-a-Lisp language; count the language once, track its many implementations separately. |
| [malc](https://github.com/dubek/malc) | LLVM / native / MAL | Compiler implementation of MAL; not another Clojure dialect. |
| [miniMAL](https://github.com/kanaka/miniMAL) | JavaScript / Python / CLJS | Independent small Lisp derived from MAL with different syntax. |
| [Phel](https://github.com/phel-lang/phel-lang) | PHP | Closely related language commonly included in dialect lists; own language and PHP compiler. |
| [Piglet](https://github.com/piglet-lang/piglet) | JavaScript ES6 | Piglet explicitly says it is a different language, not a Clojure implementation. |
| [Pixie](https://github.com/pixie-lang/pixie) | RPython / native JIT | Clojure-inspired language with its own runtime and standard library. |
| [Purisp](https://github.com/mrsekut/purisp) | PureScript / JavaScript | Small Lisp project; README documents REPL build, lineage less fully documented. |
| [Rhine](https://github.com/artagnon/rhine-ml) | OCaml / LLVM | Independent Clojure-inspired Lisp compiler. |
| [Risp](https://github.com/shybyte/risp) | Rust | Small configuration Lisp; author describes it as an approximate Clojure subset. |
| [RubyLisp](https://github.com/daveyarwood/rubylisp) | Ruby | Independent Lisp exposing Ruby's facilities. |
| [Rusjure](https://github.com/rusjure/rusjure) | Rust / LLVM | Independent compiler and language highly inspired by Clojure. |
| [Sheaf](https://github.com/sheaf-lang/sheaf) | Rust / StableHLO / IREE | Independent Clojure-inspired tensor and machine-learning language. |
| [SLisp](https://github.com/bailesofhey/slisp) | JavaScript | Independent experimental Lisp drawing on Clojure and other languages. |
| [Toccata](https://github.com/Toccata-Lang/toccata) | C / Clang | Independent Clojure-inspired Lisp compiling to native executables. |
| [Venice](https://github.com/jlangch/venice) | JVM / Java | Independent Clojure-inspired sandboxed scripting Lisp. |
| [wisp](https://github.com/wisp-lang/wisp) | JavaScript | Clojure-like Lisp with native JavaScript values and compilation. |
| [zygomys](https://github.com/glycerine/zygomys) | Go | Zygo / Zygomys: independent embeddable Lisp borrowing Clojure syntax. |

## D — Tools and syntax layers

Retained so that familiar names are accounted for rather than silently omitted. These are not counted as executable Clojure dialects.

| Project / source | Host or target | Classification and hello-world implications |
| --- | --- | --- |
| [LispSyntax.jl](https://github.com/swadey/LispSyntax.jl) | Julia | Syntax translator to Julia AST; README explicitly says it is not a Clojure implementation. |
| [Lithium](https://github.com/nathell/lithium) | x86 | Assembler written in Clojure plus toy Lisp compiler; not evidence of Clojure implementation. |
| [Liz](https://github.com/dundalek/liz) | Zig | S-expression frontend; author explicitly says it lacks features needed to be a Clojure dialect. |
| [Scriptjure](https://github.com/arohner/scriptjure) | JavaScript generation from JVM Clojure | Library generating JavaScript strings from forms; not a standalone Clojure runtime. |

## E — Proposals and attempts without an established implementation

These are real public projects or historically attested attempts. The evidence reviewed did not establish an executable language entry point. Their inclusion must not be presented as proof of a runnable dialect.

| Project / source | Host or target | Classification and hello-world implications |
| --- | --- | --- |
| [Clarp](https://github.com/halgari/clarp) | .NET / C# | Runtime code and tests exist; no documented evaluator or hello-world entry point established. |
| [clojure-metal](https://github.com/clojure-metal/clojure-metal) | Native (intended) | README remains a generated placeholder; no runnable implementation established. |
| [ClojureScript (Chris Houser early attempt)](https://clojure.org/news/2011/07/22/introducing-clojurescript) | JavaScript | Official 2011 announcement acknowledges an earlier, distinct JavaScript implementation; source/build not located in this review. |
| [ClojureSwift](https://github.com/sventech/ClojureSwift) | Swift / LLVM (intended) | Repository contains proposal/literature review and license; no implementation established. |

## How this becomes hello world

Each implementation attempt should eventually record: a pinned upstream version or commit; install/build instructions; the smallest source program; the exact invocation or embedding harness; observed output; and any platform requirement or blocker. The JSON catalogue preserves the initial research snapshot; current implementation status is tracked in the table below.

Use printed text for command-line targets, visible text or a console message for browser/editor targets, and a tiny host program for interpreter libraries. For obsolete Flash, Ruby or LLVM environments, keep the target in the catalogue and document whether a reproducible historical build is possible. Do not substitute a newer unrelated implementation under an old project's name.

A successful hello world establishes only that the chosen execution path works. It does not establish Clojure language conformance. [Clojure Census](https://github.com/leifericf/clojure-census) and the [cross-dialect Clojure test suite](https://github.com/jank-lang/clojure-test-suite) are useful separate resources for that question.

## Platform-idiomatic hello-world applications

The demos should introduce both the language and its host: **a small application that feels at home on the platform**. This is the design direction for implementation, refining the generic execution checks above. In particular, ClojureDart should have a Flutter app and ClojureScript should have a web app.

Use a common theme—**“Hello, <name>!”**—with one interaction or observable event. Each demo should show one characteristic platform feature. The ideas below are proposals, not verified claims that every listed implementation already exposes the necessary APIs. For partial and historical ports, check the pinned version's capabilities before choosing the specific integration.

### Suggested applications

| Dialect / environment | Smallest meaningful application | Platform idiom | Optional next step |
| --- | --- | --- | --- |
| [ClojureDart](https://github.com/Tensegritics/ClojureDart) | Flutter screen with a name field and a button that updates a greeting. | Widget composition, UI state and Flutter's development workflow. | Run the same app on desktop and a phone. |
| [ClojureScript](https://github.com/clojure/clojurescript) | Replicant single-page app with a name form, live greeting and greeting-history route. | DOM events, immutable state and interactive browser development. | Persist the name in local storage. |
| [Clojerl](https://github.com/clojerl/clojerl) | Two local BEAM nodes: a client actor sends a greeting request to a greeter actor on the other node and receives a reply identifying that node. | Lightweight processes, mailboxes and distributed message passing. | Supervise the greeter, deliberately crash it, then greet again after restart. |
| Clojure / JVM | Small HTTP service returning a greeting and a request number from an atom; change the greeting function through the REPL while the service runs. | Long-lived JVM services, shared immutable data and REPL-driven development. | Add a Java standard-library call to include the current time. |
| ClojureCLR | Small .NET console app that formats a greeting through a .NET API and calls a Clojure-defined function from a tiny C# host. | CLR interop in both directions. | Use the same function in a .NET desktop UI. |
| [jank](https://github.com/jank-lang/jank) | Native window with a simple shape or greeting drawn through a C++ graphics library. | Direct C++ interop and interactive native development. | Change a colour or greeting through the REPL while the window stays open. |
| Basilisp | Read a tiny CSV of names with Python's standard library and produce greetings; expose the greeting function to a Python caller. | Python modules, data processing and bidirectional interop. | Render a small pandas table in a notebook. |
| Babashka | Executable greeting script accepting a name as a command-line argument or on stdin, with a task to run it. | Fast shell scripting, pipelines and task automation. | Generate a greeting file for each name in a small input file. |
| Glojure / cljgo | Tiny HTTP greeting service using Go's `net/http` facilities, built as a native executable where supported. | Go package interop and deployment in a lightweight OCI image. | Handle requests through a goroutine/channel worker. |
| Gloat | Package the Glojure greeting app as a standalone binary. | Ahead-of-time compilation and cross-compilation. | Build for a second OS or architecture and record a separate execution result. |
| Joker / go-joker / let-go | Small command-line greeter that reads a config file and reports a useful error for a missing name. | Compact Go-based interpreters and scripting. | For implementations exposing Go packages, call one from the script. |
| nbb | Node.js script that reads a names file asynchronously and prints greetings using a small npm package for terminal presentation. | npm modules, filesystem access and promises. | Turn it into an interactive terminal prompt. |
| Squint / Cherry / Kiso | Small greeting component imported into a JavaScript web app as an ES module. | Interoperation with ordinary JavaScript modules and frontend tooling. | Import and call a JavaScript package from the component. |
| Scittle | One HTML file containing a Clojure script that changes a greeting when a button is clicked. | Browser scripting directly in script tags. | Add a second independently scripted widget. |
| Planck | [Evaluate a command-line expression](examples/planck/) using a three-line ClojureScript script. | Self-hosted compilation and runtime evaluation on JavaScriptCore. | Evaluate a form that constructs and evaluates another form. |
| Choq | Greeting script that runs as a small standalone command using its embedded JavaScript engine. | A compact runtime without an external Node installation. | Serve the greeting over HTTP if the pinned runtime supports it. |
| Joyride | VS Code command that asks for a name and inserts a greeting into the current document. | Editor commands, input UI and document APIs. | Expose the command through a keybinding. |
| Cljbang / ClojureElisp | Interactive Emacs command that asks for a name and opens a greeting buffer. | Interactive commands, buffers and Lisp extensibility. | Add a small minor mode with a greeting keybinding. |
| TimL | Vim command that greets the user in a scratch buffer. | Vim commands, buffers and mappings. | Update the greeting from the word under the cursor. |
| obb | macOS automation script that prompts for a name using a native dialog and displays a greeting. | Open Scripting Architecture and application automation. | Read the title of the active browser tab and include it in the greeting. |
| Swish | Swish-defined screen, state and event handlers rendered by a small SwiftUI host. | A native Swift application with an embedded scripting layer. | Reload the greeting script without rebuilding the host app. |
| Arcadia | Unity scene with a greeting sign and a cube that rotates when clicked. | Game objects, components and frame updates. | Change the rotation behaviour through the REPL. |
| Rust-targeted Clojure (implementation open) | Explore Raspberry Pi firmware: emit a serial greeting or blink an LED, only if the compiler supports a suitable bare-metal target. | Rust toolchain integration, board startup and hardware I/O. | Establish `no_std`, allocation and peripheral-access support first; deferred for this batch. |
| Ferret | Microcontroller blinks an LED and writes a greeting over serial. | Firmware, hardware I/O and an embedded main loop. | A physical button triggers the greeting. |
| Esprit | ESP32 prints a greeting over serial and toggles a GPIO LED from ClojureScript. | Interactive programming on a JavaScript-capable microcontroller. | Change the blink interval through the REPL. |
| ClojureFnl / ClojureScript-Lua; Fennel in the related-language collection | Small LÖVE window displaying a greeting, with a keypress changing its colour. | Lua embedding and an update/draw event loop. | Animate the greeting across the window. |
| Cloture / CLClojure | Common Lisp host loads a Clojure greeting function, calls it, and receives a string. | Sharing a Lisp image and calling across language/package boundaries. | Redefine the function while the image remains running. |
| Lokke / Cloje | Scheme host and Clojure-family code exchange a greeting through a function call. | Hosted language integration with Guile or CHICKEN/Racket as appropriate. | Embed the greeting function in a small host application. |
| Jolt | Run the same fib.clj directly with clj and jolt: ordinary tail calls complete on Jolt and overflow on the JVM. | Proper tail calls inherited from Chez Scheme, without loop/recur. | Run a self-interpreter tower on the same host. |
| JO Clojure / nanoclj / mino | Tiny C or C++ app registers a host `display-greeting` function and invokes it from an embedded script. | Embeddable interpreters and host callbacks. | Reload the script while the host loop continues running. |
| clojurust / cljrs | Native command-line greeter using one documented Rust/foreign-function boundary where available. | Native execution and integration with the implementation's host. | For cljrs, use its GPU path to draw or transform a tiny greeting image. |
| ClojureWasm | Clojure code calls a tiny Wasm export that returns a greeting count, then prints the greeting and count. | Calling a WebAssembly module from the native Clojure runtime. | Replace the Wasm module with one built from a different language. |
| Cream | [Create a JVM interface and implementing class at runtime](examples/cream/), then call its greeting method. | Full Clojure runtime compilation through Crema and Ristretto in a native executable. | Load a library after startup. |
| uclj | Native executable that reads a name from stdin and prints a greeting. | Deploying a Clojure runtime as a native executable. | Explore its supported runtime evaluation facilities. |
| Lingy | Greeting program using a Perl/CPAN module; call the greeting function from a Perl host. | Perl's library ecosystem and embedded language interop. | Package the greeting as a small Perl module. |
| YAMLScript | YAML document computes a greeting from a supplied name and emits the resulting data. | Executable configuration and code-as-data. | Load the same document through one host-language binding. |

### Ideas for the related-language collection

These follow the broader category-C scope; they should remain labelled as related languages rather than silently becoming Clojure compatibility examples.

| Language / family | Application idea | Platform idiom |
| --- | --- | --- |
| Phel / Frock | PHP-served greeting page with a name form. | Request/response web programming and PHP deployment. |
| Hy | Small notebook cell that turns a list of names into a Python table of greetings. | Python data tools and notebook exploration. |
| Sheaf | Compute a tiny tensor transformation and render a bitmap spelling “HI”. | Tensor operations and accelerated computation; use the smallest supported execution backend. |
| Carp | Native window with a moving greeting or shape. | Compiled interactive graphics and explicit resource lifetimes. |
| Janet / Pixie | C host loads a script that generates a greeting, using the implementation's documented embedding or FFI facilities. | Native scripting and host integration. |
| Glisp | Generative greeting card made from text and a few parameterized shapes. | A visual document that is also an editable Lisp program. |
| Convex Lisp | Local VM stores a greeting and answers a query for it. | Persistent VM state and contract-style execution; a local demo needs no public deployment. |
| Apricot / Rouge / Carbonate / RubyLisp | Ruby host loads a greeting function and uses it while rendering a tiny template. | Ruby objects, blocks and library interop, where the particular implementation supports them. |
| llr | Generate a small labelled plot from R data, with the greeting as its title. | R's data and plotting workflow. |
| Clojette | In-game GreyHack terminal program prompts for a name and greets the player. | Scripting within a game environment. |
| MAL / miniMAL | Embedded REPL evaluates a user-defined greeting function. | A small, inspectable interpreter and language construction. |

### Keeping the examples small and demonstrable

The first three concrete targets are **a Flutter greeting screen, a browser greeting app, and a two-node actor greeting exchange**. Clojerl's two nodes can run in two terminals on one machine: distribution is visible without requiring cloud infrastructure. First establish a local actor request/reply, then move the greeter to the second node; supervision is a subsequent demonstration, not a prerequisite for the first greeting.

For every app, document one launch command or short launch sequence, one action to perform, and the expected visible result. Include a screenshot for graphical apps or a short transcript for command-line and actor demos. Hardware examples should name the exact board and wiring; a simulator run must be labelled as such.

Keep the base example to one screen, endpoint, actor exchange, host callback or device interaction. Optional extensions belong in a second step. For a limited implementation that cannot support its host's richer APIs, use a smaller host-interop example and record the limitation. Historical compiler backends can compile a greeting into their target language and link it into the smallest possible host application.
