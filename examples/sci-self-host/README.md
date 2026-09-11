# SCI interpreting SCI, with a host runtime

**Working partial self-hosting on the JVM.** The adapted runner loads SCI's
actual analyzer, evaluator, interpreter, public API and supporting source into
SCI. Five runtime/type-support namespaces remain host-compiled. This is more
than calling the host's `eval-string` recursively, but is not completely
interpreted SCI.

```sh
cd examples/sci-self-host
clj -M tower.clj -n 2 '(str "hello" " world")'
# stdout: "hello world"
```

`-n 0` uses the host SCI; `-n 1` loads one interpreted copy; `-n 2`
uses that copy to load and execute another copy of SCI. Every layer shares
the declared native runtime below. Each parent interprets its child's source;
these are not independent calls to the host evaluator.

Depths **0, 1 and 2 are verified with default JVM stack settings**. Larger
values are accepted but experimental: stack growth and further compatibility
issues can prevent completion. Ten layers have not been demonstrated.

The runner retains parent contexts and dynamically scoped bindings as it
descends. Progress and evaluation-node counts go to stderr; a non-nil result
is printed to stdout. Node counts record which interpreter produced each node:
the depth-two test traverses nodes from both layers during final evaluation.

For now, return a value as above. Interpreted printing can format strings
incorrectly; the CLI's host-side result printing is verified.

```sh
python3 test-tower.py
clj -M adapted.clj
# Optional expression after the single-layer checks:
clj -M adapted.clj '(mapv inc [1 2 3])'
```

The tower tests check depths 0–2, real layer traversal, closures, recursion,
`loop/recur`, macros, records/protocols, dynamic bindings, invalid arguments
and error propagation. [Tower test output](evidence/tower.txt).

The single-layer runner prints a greeting and checks arithmetic, closures, recursion,
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

The omitted source form is the evaluator's `extend-protocol types/Eval`
block for constants: the supplied host protocol already implements those same
identity operations. Unsupported mutable types are retained in the host
runtime rather than replaced with incomplete substitutes.

Two binding helpers are also adapted: `binding` expands to one
`with-bindings*` call, whose complete push/call/pop scope runs in a native
helper. Otherwise an intermediate interpreter restores its temporary binding
frame between the inner push and pop, silently losing the inner binding.
Dynamic-binding tests at both interpreted depths cover this workaround.

The shared adapter lives in [self_host.clj](self_host.clj); the recursive
driver is [tower.clj](tower.clj).

## How the check rules out a host-eval shortcut

After loading the implementation, the runner obtains the interpreted
`eval-string` function. During every interpreted execution it replaces the host's
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
