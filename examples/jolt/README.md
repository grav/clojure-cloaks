# Jolt: Fibonacci with ordinary tail calls

The same [fib.clj](fib.clj) runs on Jolt and JVM Clojure. It contains two versions:

```clojure
(defn fib-step [n a b]
  (if (zero? n)
    a
    (fib-step (dec n) b (+' a b))))

(defn naive-fib [n]
  (if (< n 2)
    n
    (+' (naive-fib (- n 1))
        (naive-fib (- n 2)))))
```

The first calls itself in tail position: there is nothing to do after that
call returns. Jolt's Chez Scheme host supports proper tail calls. JVM Clojure
uses stack for these ordinary calls. There is no `loop`, `recur`, trampoline,
or hand-written Scheme in either Fibonacci implementation.

The second must add both results after recursion returns, so TCO cannot remove
those pending additions. It also does exponentially more work. The demo runs
that version only at n=20, then runs the accumulator version at n=20000.
Both use exact integer arithmetic; the accumulator starts with `0N` and `1N`.
TCO bounds call-stack growth, not the memory needed for the growing integers.

## Run

```sh
cd examples/jolt
./run.sh          # n=20000
./run.sh 10000    # optional n, from 0 through 100000
```

`run.sh` runs Jolt directly on the host, without Docker. On first use,
`setup.sh` builds threaded Chez Scheme 10.4.1 and checks out the Jolt revision
in `JOLT_REV`. It installs into `~/.cache/clojure-dialects/jolt`, without root
access or changing system packages. Override that location with `JOLT_DEMO_CACHE`.

The setup needs Git, a C compiler, Make, and development headers for ncurses,
zlib, LZ4, UUID, and X11. They were already available on this Linux ARM64 VM.
Run `./setup.sh` separately to install the toolchain in advance. `./jolt.sh`
exposes the native Jolt CLI, for example `./jolt.sh -e '(+ 1 2)'`.

The result is summarized as digit count, first/last 30 digits, and a modulus,
rather than printing thousands of digits.

With Jolt already installed, `jolt demo.clj 20000` runs the same program.

## JVM comparison and tests

With Java and the Clojure CLI installed:

```sh
clojure -J-Xss256k -M compare-jvm.clj
python3 test.py
```

The JVM comparison deliberately sets a 256 KiB thread stack and catches the
expected `StackOverflowError`. This gives a reproducible demonstration; the
failure depth depends on JVM settings. It is not a speed benchmark.

The tests compare Jolt results at n=0, 1, 2, 20, and 20000 against an independent
Python fast-doubling implementation, then run the JVM comparison.
Verified on Linux ARM64 with Jolt `v0.8.6-72-g0f7d1a11`, threaded Chez
Scheme 10.4.1, and JVM Clojure 1.12.5. All five input checks and the controlled
JVM overflow check passed. The large result has 4180 decimal digits and is
988094463 modulo 1000000007. Tests compare digit count, leading/trailing digits,
and modulus against the reference calculation.

[Jolt output](evidence/jolt.txt) · [JVM output](evidence/jvm.txt).

Jolt source revision: `0f7d1a11d951047448dcf02f0cbda48c6b74083a`.
Chez source revision: `e95a7efbafa2cf3bd5343ea542e6bc909a7ab2c4` (v10.4.1).

References: [Jolt](https://github.com/jolt-lang/jolt),
[the author's explanation of TCO](https://yogthos.net/posts/2026-07-02-jolt.html),
[Clojure's explicit recur semantics](https://clojure.org/reference/special_forms#recur).
