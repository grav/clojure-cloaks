# Research method and scope

Checked on **2026-09-11**. This is discovery and classification, not installation, execution or a compatibility audit.

## Inclusion rule

The main catalogue includes publicly documented implementations of Clojure, interpreters for meaningful subsets of it, explicitly self-described Clojure-family dialects, and compilers accepting Clojure/ClojureScript forms. Historical and unfinished projects are included. It also records separately packaged runtimes because the ultimate objective is hello-world coverage across execution environments.

Category A is deliberately broader than a strict language taxonomy. A ClojureScript backend, a partial Clojure implementation and the reference JVM implementation can all be separate implementation targets without being separate language designs. Self-description is evidence of intent, not proof of compatibility.

Related Lisps appear separately in C. Phel, for example, appears in many community dialect lists but presents itself as a distinct PHP language. Piglet explicitly disclaims being a Clojure implementation. Calcit explicitly describes itself as a ClojureScript dialect despite its different surface syntax and increasingly different semantics. FOL calls itself a Clojure dialect while adding substantial features. These boundary decisions are visible rather than hidden in an inflated total.

## Discovery coverage

The research reconciled these four catalogues:

| Discovery source | Coverage in this review |
| --- | --- |
| [ilevd/clojure-like](https://github.com/ilevd/clojure-like) | All 111 main-table projects accounted for; used for discovery rather than copying its classifications or maintenance labels |
| [clj-easy/clojure-dialects-docs](https://github.com/clj-easy/clojure-dialects-docs) | Every dialect section accounted for, including cljrs, Scittle, Joyride, Las3r and Scriptjure |
| [chr15m/awesome-clojure-likes](https://github.com/chr15m/awesome-clojure-likes) | Language/runtime entries reconciled; supplementary application/library links excluded |
| [Clojure.cc catalogue](https://clojure.cc/dialects/) and its [source data](https://github.com/clojurestar/clojure-cc/blob/main/src/dialects.yaml) | All project entries reconciled; added obb and YAMLScript; noted that the catalogue itself distinguishes Clojure from adjacent Lisps |

Additional searches covered new dialects and implementations in 2023–2026, historical ports, native/LLVM targets, Python, Swift, Rust, Go, Scheme and Perl. Primary-source following added Torque, the historical clojure-py fork, the ClojureSwift proposal, and the earlier ClojureScript attempt acknowledged in the official announcement. [Clojure Census](https://github.com/leifericf/clojure-census) supplied a further cross-check and a useful distinction between API surface and behavioral conformance.

Search queries included `Clojure dialects implementations list 2026`, `new Clojure dialect 2025 2026 github`, `Clojure implementations dialects clojure-py clojure lua beam historical`, year-specific dialect searches, `Torque Clojure Jan Krueger github`, and `CljPerl`. Search results and community posts were leads; classification relied on project READMEs, project pages, source manifests and official historical accounts. Two broad GitHub repository-search API requests timed out and are not counted as successful coverage.

## Evidence and freshness

Most entries were checked by retrieving their upstream README directly, including nonstandard filenames such as `ReadMe.md`, `README.mkd`, `README.org` and `readme.txt`. GitLab raw READMEs resolved some pages that the browser extractor could not read. For projects without usable READMEs, repository descriptions, directory listings or package manifests were checked. The JSON lists the evidence URL and access date for each entry. Documentation hashes identify the retrieved content without redistributing upstream documentation.

The `repository_metadata` objects are snapshots of selected GitHub/GitLab metadata, chiefly for recent projects. A missing metadata object means it was not collected; it does not mean the repository is old, inactive or unavailable. `created_at` is repository creation, `pushed_at` is repository activity, and `archived` is the host's archive flag. None is a substitute for first-release history or a maintainer's statement.

For example:

- [go-joker's metadata](https://api.github.com/repos/rcarmo/go-joker) dates its fork repository to 2019. Its inclusion among recent announcements is a different fact.
- [Glojure's metadata](https://api.github.com/repos/glojurelang/glojure) dates its repository to November 2022; the community history records a first release in 2023.
- [ClojureWasm](https://github.com/clojurewasm/ClojureWasm) explicitly says it is unmaintained even though the repository is not archived.
- [Kiso's metadata](https://api.github.com/repos/clojurewasm/Kiso) marks the repository archived.
- [Cljam](https://github.com/RegiByte/cljam) describes itself as complete and not actively maintained despite recent repository activity.
- The [new clojure-py manifest](https://github.com/clojure-py/clojure-py/blob/main/pyproject.toml) describes a Cython implementation for Python 3.14, while the repository description says PyO3. The catalogue records that discrepancy instead of selecting an unsupported implementation story.
- [Las3r](https://github.com/aemoncannon/las3r) targets **AVM2 with ActionScript 3 interop**. AVM2 should not be confused with ActionScript 2.

Claims that a language is fast, complete, conformant or production-ready were not independently validated. Those claims are unnecessary to establish that it is a project worth considering for hello world.

## Identity and deduplication

Repository identity disambiguates similar names. The JSON uses URL-derived stable identifiers, except for the historically attested early ClojureScript attempt whose code location is unresolved.

- **Rust:** `clojure-rs/ClojureRS`, `tytrdev/cljrs`, `csm/clojurust`, `clojurust/clojurust`, `naomijub/ClojuRS` and `chimez/clojure-rust` are separate projects. Rustly and Rusjure are also separate.
- **Go:** `glojurelang/glojure`, `venantius/glojure`, `muthuishere/cljgo` and `tcard/gojure` are separate. Joker, go-joker and let-go are separate implementations. Gloat is tooling around Glojure; `gloathub/glojure` is a temporary implementation fork, not another language design.
- **Python:** the historical `halgari/clojure-py` work survives in `drewr/clojure-py`; these are one historical lineage in this inventory. The 2026 `clojure-py/clojure-py` repository is recorded separately. Basilisp is another implementation, and Hy is a related independent language.
- **Swift:** Cormorant, formerly Lambdatron, is separate from Swish. ClojureSwift is a proposal whose repository contains a literature review and license rather than compiler source.
- **Emacs:** Cljbang / `clj!` and ClojureElisp are separate projects.
- **JavaScript:** Choq is specifically Cherry on QuickJS. Lumo and Planck run ClojureScript; nbb and Scittle use SCI.
- **Historical names:** Arcadia was clojure-unity; ClojureScript-Terra has an earlier design called CLIC; YAMLScript is also YS. These aliases do not create extra entries.
- **Javelin:** `kimtg/Javelin` is a Lisp implementation; Hoplon's Javelin is a different library.

The source list links `vic/rouge`, whose README also mentions `unnali/rouge`; these are not counted twice. The historical `clojure-lua` lead is represented by the verified `raph-amiard/clojurescript-lua` project rather than a second unverified entry.

## Explicit exclusions and unresolved boundaries

Ordinary libraries, data readers and build tools are not independent dialects merely because they consume Clojure forms. This excludes Leiningen, Boot, tools.build, shadow-cljs, Figwheel, nREPL, CIDER, Calva, clj-kondo, tools.reader, EDN parsers and interop libraries such as libpython-clj from the language count. Typed Clojure adds checking to Clojure; it is not independently counted here. Platform deployments of ordinary Clojure or ClojureScript are not enumerated as new dialects merely because the OS, JVM vendor, JavaScript engine or hardware changes.

For completeness, Scriptjure, LispSyntax.jl, Liz and Lithium have explicit category-D rows. LispSyntax.jl and Liz explicitly distinguish their syntax layers from a Clojure implementation. Scriptjure generates JavaScript strings; Lithium combines an assembler with a toy Lisp compiler.

MAL is a particularly important scope boundary. Its [upstream README](https://github.com/kanaka/mal) describes **95 implementations in 89 languages, with 118 runtime modes** at retrieval. Those are not 95 Clojure dialects. This inventory records MAL once and separately notes the related malc compiler and miniMAL language. A future “all implementations of all Clojure-inspired languages” objective would need a dedicated MAL implementation matrix and a much larger scope.

Likewise, YAMLScript's many host-language bindings do not become separate dialects, and Lingy's experimental host directories do not establish completed ports. The public source inventory is not an exhaustive census of every SCI embedding, fork, tutorial exercise or private interpreter.

Remaining uncertainties are retained visibly:

1. The earlier Chris Houser ClojureScript implementation is acknowledged by the [official 2011 introduction](https://clojure.org/news/2011/07/22/introducing-clojurescript). Its source/build location was not established.
2. Clarp has runtime source and tests, but no documented executable language entry point was established. Clojure-metal's README is still a generated placeholder. Both remain category E pending code/build investigation.
3. Bara's GitLab project is archived. Its linked [Codeberg project](https://codeberg.org/baraba/bara-lang) needs a separate build/continuation check; it is not counted as a second language.
4. Partial implementations such as ClojureHaxe, clojure-clr-next, the new clojure-py and historical venantius/Glojure may not yet support printing a string. Project existence is established; hello-world execution is not.
5. Related-language classification is a judgment call. An intentionally broad demo collection can include category C without describing every entry as Clojure-compatible.

## Next phase

There are **80 A/B project targets**, not 80 guaranteed runnable languages. Begin with those targets and record a pinned build, a minimal example, actual output and platform requirements. Keep an explicit unsupported/blocked result for incomplete or unrecoverable implementations. Add C as a separately labelled extension if broad family coverage is desired.

A working `(println "Hello, world!")` is only an execution smoke test. It says nothing about immutable collection semantics, macro expansion, JVM interop, concurrency, or compatibility with existing Clojure libraries.
