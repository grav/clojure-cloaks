# SCI from Rust through GraalVM

From `examples/sci-rust`, after the one-time build below, evaluate an expression
with one command:

```sh
./build/sci-eval '(str "hello world")'
```

Output:

```text
hello world
```

[eval.rs](eval.rs) creates a GraalVM isolate, calls SCI's `eval_string` C entry
point, copies the returned string, shuts down the isolate, and prints the result.
SCI is compiled into `libsci.so`; running the Rust executable needs no JVM process.
This is ordinary Rust calling a native library, not a Rust-targeted Clojure dialect.

## Build on Linux

Install a Rust toolchain, a C compiler and zlib development files, Clojure CLI,
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

Copy the library beside the executable and compile the Rust caller:

```sh
cd examples/sci-rust
mkdir -p build
cp ../../.cache/sci-libsci/libsci/target/libsci.so build/
rustc --edition=2024 eval.rs -L native=build -C 'link-arg=-Wl,-rpath,$ORIGIN' -o build/sci-eval
```

The embedded library search path finds `libsci.so` beside the executable, so no
`LD_LIBRARY_PATH` setting or launcher script is needed. Keep both files together.
The FFI declarations match the generated headers for the pinned Linux ARM64 build.

The upstream wrapper converts results with Clojure `str`: strings print without
quotes, and `nil` produces an empty line. It returns evaluation errors as map
strings; these do not cause a nonzero process exit. The pure Clojure-host example
uses `prn` and therefore prints strings with quotes.

To display a lazy collection, request its printed representation explicitly:

```sh
./build/sci-eval '(pr-str (map inc [1 2 3]))'
```

Verified natively on Linux ARM64 with Oracle GraalVM 21.0.12 and SCI 0.15.58:
greeting, arithmetic, Unicode, collection formatting, `nil`, missing arguments,
and running the relocated executable/library pair without Java or library-path
environment variables. The executable is about 525 KB and `libsci.so` is 36.4 MB.
