# SCI through C FFI: Rust and Zig

From `examples/sci-ffi`, after the one-time build below, evaluate an expression
with one command:

```sh
./build/sci-rust '(str "hello world")'
./build/sci-zig '(str "hello world")'
```

Output:

```text
hello world
```

[Rust](eval.rs) and [Zig](eval.zig) call the same `eval_string` C entry point in
`libsci.so`. Each creates a GraalVM isolate and shuts it down after retrieving
the result. Rust copies the result before shutdown; Zig prints it while the
isolate is alive. Neither executable needs a JVM process.

Clojure compiles SCI to JVM bytecode, then GraalVM Native Image compiles that
bytecode to native machine code with runtime support such as garbage collection.
It exports a C-compatible interface; it does not translate SCI into C. SCI still
evaluates the supplied Clojure expression at runtime.

## Build on Linux

Install Rust and/or Zig 0.16.0, a C compiler and zlib development files, Clojure CLI,
Babashka, Leiningen, and [GraalVM for JDK 21](https://www.graalvm.org/jdk21/docs/getting-started/linux/)
with Native Image. Set `GRAALVM_HOME` to that installation. On this VM:

```sh
export GRAALVM_HOME="/home/grav/repo/clojure-dialects/.cache/graalvm21"
export JAVA_HOME="$GRAALVM_HOME"
export PATH="$JAVA_HOME/bin:$PATH"
```

From this repository's root, build the pinned
[upstream SCI shared library](https://github.com/babashka/sci/blob/ebd3462b9e777d4d0b83ad13b81e526707391354/doc/libsci.md):

```sh
mkdir -p .cache
git clone https://github.com/babashka/sci .cache/sci-libsci
cd .cache/sci-libsci
git checkout ebd3462b9e777d4d0b83ad13b81e526707391354
bb libsci:compile
cd ../..
```

Copy the library and generated C headers into the build directory:

```sh
cd examples/sci-ffi
mkdir -p build
cp ../../.cache/sci-libsci/libsci/target/libsci.so build/
cp ../../.cache/sci-libsci/libsci/target/*.h build/
```

Compile either or both callers directly:

```sh
rustc --edition=2024 eval.rs -L native=build -C 'link-arg=-Wl,-rpath,$ORIGIN' -o build/sci-rust
zig build-exe eval.zig -I build -L build -lsci -lc -fno-lld -rpath '$ORIGIN' -femit-bin=build/sci-zig
```

The embedded library search path finds `libsci.so` beside the executable, so no
`LD_LIBRARY_PATH` setting or launcher script is needed. Keep each executable beside the shared library. Zig's `-fno-lld` selects its
alternative linker, which records `libsci.so` rather than `build/libsci.so`
for this library without a SONAME.
Rust declares the pinned C ABI explicitly; Zig imports the generated headers
using `@cImport`. Headers are needed only at build time.

The upstream wrapper converts results with Clojure `str`: strings print without
quotes, and `nil` produces an empty line. It returns evaluation errors as map
strings; these do not cause a nonzero process exit. The pure Clojure-host example
uses `prn` and therefore prints strings with quotes.

To display a lazy collection, request its printed representation explicitly:

```sh
./build/sci-rust '(pr-str (map inc [1 2 3]))'
```

Verified natively on Linux ARM64 with Oracle GraalVM 21.0.12, SCI 0.15.58,
and Zig 0.16.0:
greeting, arithmetic, Unicode, collection formatting, `nil`, missing arguments,
and running both relocated executable/library pairs without Java or library-path
environment variables. Both callers share the same 36.4 MB `libsci.so`.
