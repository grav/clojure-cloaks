# SCI interpreting SCI, with a host runtime

**Working partial self-hosting on the JVM.** The adapted runner loads SCI's
actual analyzer, evaluator, interpreter, public API and supporting source into
SCI. Five runtime/type-support namespaces remain host-compiled. This is more
than calling the host's `eval-string` recursively, but is not completely
interpreted SCI.

```sh
cd examples/sci-self-host
clj -M adapted.clj
# Optional expression, evaluated after the checks:
clj -M adapted.clj '(mapv inc [1 2 3])'
```

The runner prints a greeting and checks arithmetic, closures, recursion,
`loop/recur`, macros, atoms, exceptions, records/protocols, metadata,
destructuring, multimethods, `reify`, `deftype`, dynamic binding and context
isolation. These checks pass on Linux ARM64. [Captured output](evidence/adapted.txt).

## What is interpreted, and what is supplied

The source loader renames implementation symbols into `self.sci.*` so it
cannot silently reuse the outer interpreter's built-in namespaces. It preserves
quoted namespace names used in the inner language's namespace tables.

The declared host SCI runtime is exactly:

- `sci.impl.types`: evaluation interfaces and node/type machinery.
- `sci.impl.vars`: mutable vars and thread-binding machinery.
- `sci.lang`: JVM representations of vars, namespaces and types.
- `sci.impl.records` and `sci.impl.deftype`: concrete type implementations
  and their support functions.

The entire contents of these five namespaces are supplied, not just their
constructors. Their analysis/resolution callbacks are redirected to the
**interpreted** analyzer and resolver while running the inner evaluator.

Reader libraries, Java I/O, locking, standard Clojure primitives and JVM classes
also come from the host. The loader uses the host reader to prepare the
implementation source; the resulting interpreted SCI uses Edamame to read
user programs.

A Java proxy implements the interfaces required by SCI's evaluation nodes.
Its method bodies are interpreted functions, installed through SCI's existing
`:reify-fn` hook. No JVM compiler evaluates the inner user's program.

The only omitted source form is the evaluator's `extend-protocol types/Eval`
block for constants: the supplied host protocol already implements those same
identity operations. Unsupported mutable types are retained in the host
runtime rather than replaced with incomplete substitutes.

## How the check rules out a host-eval shortcut

After loading the implementation, the runner obtains the interpreted
`eval-string` function. During every check it replaces the host's
`sci.core/eval-string`, `sci.core/eval-string*`,
`sci.impl.interpreter/eval-string*` and `sci.impl.analyzer/analyze` entry
points with functions that throw. The checks still pass. The outer interpreter
continues executing the already-analyzed implementation's function bodies.

This is an experimental, single-threaded runner: scoped host callback
rebindings affect the process while a check runs. It uses an unrestricted
context for trusted implementation source, not a sandbox. The focused checks
do not establish full SCI compatibility, concurrency support or arbitrarily
deep self-hosting. ClojureScript and ClojureDart hosts have not been tested.

SCI is pinned in `deps.edn` to
[`ebd3462`](https://github.com/babashka/sci/tree/ebd3462b9e777d4d0b83ad13b81e526707391354).
No upstream source checkout is modified. Tested on 2026-09-11.

## Original probes

These remain available to demonstrate why an adapter is needed; each exits 1:

```sh
clj -M probe.clj
clj -M probe.clj --host-deps
clj -M type-check.clj
```

## Unmodified-source results

| Attempt | Result |
| --- | --- |
| Load SCI and dependency source using SCI's standard core environment | Stops in tools.reader: `Character/isWhitespace` is unavailable. |
| Supply host reader/locking libraries and two explicitly allowed Java classes | Reaches SCI's own `Reified` definition, then rejects its Java interface implementation. |
| Evaluate that type definition alone with its interface explicitly available | Same interface rejection. |

The second attempt supplies public vars from already-loaded `edamame.*`,
`clojure.tools.reader*` and `borkdude.*` namespaces, plus
`java.lang.IllegalStateException` and the dependency's Java
`sci.impl.types.ICustomType` interface. It supplies no host `sci.*` namespace
functions. The outer evaluator and standard SCI built-ins still run on the host,
as expected for an interpreter running another interpreter.

The decisive error is:

```text
defrecord/deftype currently only support protocol implementations, found: sci.impl.types.ICustomType
```

SCI's JVM
[`Reified` definition](https://github.com/babashka/sci/blob/ebd3462b9e777d4d0b83ad13b81e526707391354/src/sci/impl/types.cljc#L41)
implements that Java interface. Allowing access to the interface does not make
interpreted `deftype` support implementing it. The full-load error reports the
requiring callstack namespace; `type-check.clj` isolates the actual failing form.

This establishes a blocker for **unmodified JVM source**, not impossibility
across all SCI hosts or after changes. Getting further requires adapting the
type layer or extending SCI's interface support; additional blockers may follow.
We have not tested the ClojureScript or ClojureDart branches. The adapted runner above uses a hybrid approach with host-compiled runtime types.

Captured output:
[source loading](evidence/source-load.txt),
[host dependency boundary](evidence/host-deps.txt),
[minimal type check](evidence/type-check.txt).
