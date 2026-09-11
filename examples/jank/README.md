# jank: a native SDL window

jank constructs the greeting, owns the event loop and selects an immutable colour
palette. Direct C++ interop calls a small [SDL wrapper](graphics.hpp) that manages
the native window and renderer. The greeting appears in the window title and
terminal; a smiling shape appears in the window.

On Apple Silicon macOS:

```sh
brew install jank-lang/jank/jank sdl2
cd examples/jank
./run.sh
```

Click or press Space to cycle colours. Press Escape or close the window to quit.
The first invocation builds jank's precompiled header and takes longer.

Repeat the rendering/event check:

```sh
HELLO_SMOKE=1 ./run.sh
python3 check-snapshot.py
```

Smoke mode pushes a Space key event into SDL's queue, renders three frames, saves
`hello.bmp`, and exits. The Python check verifies dimensions, background and the
second palette colour, then writes `screenshot.png`. It uses only Python's
standard library.

Verified on macOS 26.5.1 ARM64 with Homebrew jank 0.1 and SDL 2.32.10:
native window launch, jank event loop, palette update and captured pixel checks.

![Native SDL canvas after a Space keypress](screenshot.png)

The Homebrew jank binary downloaded on 2026-09-11 had SHA256
`62ead9ab3b52bd9ec8e9acc269a4a4f5c58197f01e5d25f2da1c6047c55afb09`.
jank is alpha software; this example explicitly cycles integer indices because
the tested build's `mod` returned a real value unsuitable for `nth`.

## Linux ARM64 source build

```sh
cd examples/jank
./run-linux-arm64.sh --smoke
# With DISPLAY set on an X11 desktop:
./run-linux-arm64.sh
```

The wrapper builds [Dockerfile.arm64](Dockerfile.arm64) if its local image is
missing. This compiles jank natively for AArch64 using LLVM 23 and GCC 14's
standard library. It needs several GB of disk and RAM; compilation uses two jobs.
The smoke check runs the native SDL window on an Xvfb display.

Verified on Linux ARM64 on 2026-09-11: native AArch64 ELF executable,
`jank check-health` JIT and AOT checks, window rendering, synthetic Space key,
and captured palette/background pixels all passed. Docker uses `--init` so
Xvfb can signal its parent that the virtual display is ready.

The build pins jank commit `9bea4140812e995bb868eb3313dc5b65aa075768` and the
LLVM apt package version. [linux-arm64.patch](linux-arm64.patch) adds a missing
`<cmath>` include in CppInterOp and AArch64 ELF data relocations in cpptrace's
JIT debug-info reader. The latter follows the
[Arm ELF ABI](https://github.com/ARM-software/abi-aa/blob/main/aaelf64/aaelf64.rst).
The container also sets a UTF-8 locale so jank can read its Unicode source files.
These are local build fixes, not an upstream ARM64 binary release.

## Older x86-64 package under emulation

The Ubuntu x86-64 toolchain image also built and passed the same pixel checks
on this ARM64 Linux VM under QEMU. It uses SDL's offscreen dummy video driver;
the visible native window was verified separately on macOS. ARM64 Linux needs
x86-64 binfmt emulation for this upstream package (Docker Desktop usually
provides it; this VM used `tonistiigi/binfmt --install amd64`).

```sh
docker build --platform linux/amd64 -t clojure-hello/jank-toolchain:local .
docker run --rm --platform linux/amd64 -e HELLO_SMOKE=1 -e SDL_VIDEODRIVER=dummy \
  -v "$PWD:/app" clojure-hello/jank-toolchain:local \
  ./run.sh
python3 check-snapshot.py
```

Sources: [jank C++ interop](https://book.jank-lang.org/),
[SDL2](https://wiki.libsdl.org/SDL2/FrontPage).

## ARM64 fix and regression reproduction

The patch and a standalone ELF debug-info regression are preserved in
[grav/jank, linux-arm64-bootstrap](https://github.com/grav/jank/tree/linux-arm64-bootstrap/contrib/linux-arm64).
The same AArch64 object throws a DWARF error before the fix and resolves its
function, filename and line afterwards. The regression also checks an x86-64
object on the ARM64 host:

```sh
docker run --rm --init -v "$PWD:/app" clojure-hello/jank-arm64:local ./regression/check.sh
```

No upstream PR has been opened yet: jank's
[contribution policy](https://github.com/jank-lang/jank/blob/main/CONTRIBUTING.md)
excludes AI-generated external contributions. This AI-assisted bundle is in a
personal fork pending a human-led submission or clarification that the internal
contributor exception applies.
