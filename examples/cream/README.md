# Cream: create JVM types at runtime

[Cream](https://github.com/borkdude/cream) packages Clojure's compiler in a native
executable using GraalVM's Crema runtime class loading and Ristretto JIT.

Cream is installed at `~/.local/bin/cream` in this VM. From the repository root:

```sh
export JAVA_HOME="/home/grav/repo/clojure-dialects/.cache/graalvm-crema"
cd examples/cream
cream -M hello.clj Ada
```

Output:

```text
Hello, Ada!
```

[hello.clj](hello.clj) defines a JVM interface and an implementing class, then
calls the greeting method. These types are created when the script runs; they
were not present when the Cream executable was built. Clojure generates their
bytecode, Crema loads it, and Ristretto can JIT-compile runtime-loaded code.
This demonstrates runtime compilation; the greeting is not a JIT benchmark.

Cream may read JDK class files at runtime. The matching `JAVA_HOME` above avoids
its boot-class-loader warning; this does not launch a separate JVM process.

The default name is `world`. You can also evaluate an expression directly:

```sh
cream -M -e '(str "hello world")'
```

## Installation

Cream is experimental and currently uses a patched Clojure compiler and an early
access GraalVM build. See its [installation instructions](https://github.com/borkdude/cream#install)
for published binaries.

The Linux ARM64 build uses these revisions:

- Cream: `26fceebbdeeb6140822708451a58dd94e51e0f3e`
- Clojure fork: `e6f1a25d116bf301fe54ef03eaf46d4466ce996e`
- Oracle GraalVM: `jdk-25i2-25.0.3-ea.04`, Linux AArch64

With GraalVM, Maven, Clojure CLI, Babashka, a C compiler and zlib development files
installed and enough free memory for Native Image, set `GRAALVM_HOME` and `JAVA_HOME` to the GraalVM directory and put its
`bin` directory on `PATH`. Then build from the repository root:

```sh
git clone -b crema https://github.com/borkdude/clojure.git .cache/clojure-crema
cd .cache/clojure-crema
git checkout e6f1a25d116bf301fe54ef03eaf46d4466ce996e
mvn install -Dmaven.test.skip=true -q
cd ../..
git clone https://github.com/borkdude/cream.git .cache/cream
cd .cache/cream
git checkout 26fceebbdeeb6140822708451a58dd94e51e0f3e
bb build-native
install -m755 cream "$HOME/.local/bin/cream"
```

A four-gigabyte build heap was insufficient in this VM. The ARM64 release
workflow uses a dedicated GitHub runner with 16 GB of memory.

## Verification

The [native GitHub ARM64 build](https://github.com/grav/cream/actions/runs/34621339449)
produced the executable tested in this Linux ARM64 VM. Default, named, Unicode
and quoted-name greetings passed, as did direct runtime `eval`.
The example's interface and implementing class are compiled when the file runs.

Linux ARM64 release support is proposed in [Cream PR #6](https://github.com/borkdude/cream/pull/6).
The installed binary's SHA256 is
`9864608fb2b357d4169b56aa911c26597a5505557602c568e5dfc6078f10219e`.
