# Jolt: the same tail-recursive Fibonacci on two runtimes

From this directory, with Clojure and Jolt installed:

```sh
cat fib.clj
clj -M fib.clj
jolt fib.clj
```

JVM Clojure exits with `StackOverflowError`. Jolt finishes:

```text
Computing Fibonacci( 20000 ) with ordinary tail calls
digits: 4180
mod-1000000007: 988094463
```

Both commands execute the exact same [fib.clj](fib.clj), including its entry
point. There are no demo launchers or separate comparison programs. An optional
argument changes the input: `jolt fib.clj 20` or
`clj -M fib.clj 20` both finish.

The recursive call is in tail position. Jolt inherits proper tail calls from
Chez Scheme; JVM Clojure consumes stack for ordinary recursive calls. With the default JVM stack on this VM, n=20000 overflows in about
0.55 seconds including JVM startup; the failure depth depends on the JVM
and its stack settings. There is no `loop`, `recur`
or trampoline, and arithmetic uses exact integers. TCO bounds stack growth,
not the space occupied by those integers. This is not a speed benchmark.

## Toolchain setup

The toolchains are already installed on this VM and `jolt` is on PATH.
It is a symlink to the upstream Jolt launcher, which discovers the installed
Chez automatically. Bare `scheme fib.clj` cannot read Clojure: Jolt supplies
the reader, compiler and runtime.

For a fresh machine, `./setup.sh` builds threaded Chez Scheme 10.4.1, checks out
the revision in `JOLT_REV`, and creates `~/.local/bin/jolt`. It needs Git,
a C compiler, Make, and development headers for ncurses, zlib, LZ4, UUID and
X11. No Docker or root access is needed. `JOLT_DEMO_CACHE` overrides the
installation directory. Make sure `~/.local/bin` is on PATH.

## Verification

`python3 test.py` checks inputs 0, 1, 2, 20 and 20000 against an independent
Python fast-doubling calculation (digit count and modulus), then checks that
JVM Clojure fails with a stack overflow on the same file.

Verified on Linux ARM64 with Jolt `v0.8.6-72-g0f7d1a11`, threaded Chez
10.4.1 and JVM Clojure 1.12.5.
[Jolt output](evidence/jolt.txt) · [JVM output](evidence/jvm.txt).

Jolt source revision: `0f7d1a11d951047448dcf02f0cbda48c6b74083a`.
Chez source revision: `e95a7efbafa2cf3bd5343ea542e6bc909a7ab2c4`.

References: [Jolt](https://github.com/jolt-lang/jolt),
[Clojure's recur semantics](https://clojure.org/reference/special_forms#recur).
