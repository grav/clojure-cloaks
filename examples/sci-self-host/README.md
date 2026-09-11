# Testing SCI self-hosting

**Blocked on the JVM with unchanged SCI source.** This experiment loads SCI's
actual source into a fresh SCI context and asks the interpreted copy to evaluate
`(+ 1 2)`. It does not install the host's `sci.core/eval-string` into that
context.

Run from this directory with the Clojure CLI:

```sh
clj -M probe.clj
clj -M probe.clj --host-deps
clj -M type-check.clj
```

Each currently exits 1 and prints its blocker. Dependencies pin SCI to
[`ebd3462`](https://github.com/babashka/sci/tree/ebd3462b9e777d4d0b83ad13b81e526707391354).
Tested on the Linux ARM64 VM on 2026-09-11.

## Results

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
We have not tested the ClojureScript or ClojureDart branches. Replacing SCI's
type layer with host-compiled SCI types would be a separate, hybrid experiment.

Captured output:
[source loading](evidence/source-load.txt),
[host dependency boundary](evidence/host-deps.txt),
[minimal type check](evidence/type-check.txt).
