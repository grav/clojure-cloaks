# jank: a native SDL window

[hello.jank](hello.jank) calls SDL directly through jank's C++ interop. It creates
the window and renderer, reads native event structs, selects an immutable colour
palette, draws the face, and releases the SDL resources in `finally` blocks.
The greeting appears in the window title and terminal. There is no custom C++
wrapper or header.

`(:include "SDL2/SDL.h")` imports SDL's declarations; `cpp/SDL_*` calls its
functions. `cpp/.-field`, `cpp/=`, and `cpp/&` access fields, assign native values,
and take addresses. The smoke screenshot uses `SDL_SaveBMP_RW` directly because
`SDL_SaveBMP` is a function-like C macro.

On Apple Silicon macOS:

```sh
brew install jank-lang/jank/jank sdl2
cd examples/jank
sdl=$(brew --prefix sdl2)
jank -I . -I "$sdl/include" -L "$sdl/lib" -l SDL2 run hello.jank
```

Click or press Space to cycle colours. Press Escape or close the window to quit.
The first invocation builds jank's precompiled header and takes longer.

Repeat the rendering/event check:

```sh
HELLO_SMOKE=1 jank -I . -I "$sdl/include" -L "$sdl/lib" -l SDL2 run hello.jank
python3 check-snapshot.py
```

Smoke mode pushes a Space key event into SDL's queue, renders three frames, saves
`hello.bmp`, and exits. The Python check verifies dimensions, background and the
second palette colour, then writes `screenshot.png`. It uses only Python's
standard library.

Verified on macOS 26.5.1 ARM64 with Homebrew jank 0.1 and SDL 2.32.10:
direct SDL calls from jank, native window launch, palette update and captured
pixel checks.

![Native SDL canvas after a Space keypress](screenshot.png)

The Homebrew jank binary downloaded on 2026-09-11 had SHA256
`62ead9ab3b52bd9ec8e9acc269a4a4f5c58197f01e5d25f2da1c6047c55afb09`.
jank is alpha software; this example explicitly cycles integer indices because
the tested build's `mod` returned a real value unsuitable for `nth`.

## Native Linux ARM64

```sh
cd examples/jank
HELLO_SMOKE=1 xvfb-run -a ./jank.sh -I . -L "$(pkg-config --variable=libdir sdl2)" -l SDL2 run hello.jank
python3 check-snapshot.py
# With DISPLAY set on an X11 desktop:
./jank.sh -I . -L "$(pkg-config --variable=libdir sdl2)" -l SDL2 run hello.jank
```

The VM has the tested jank bundle installed in `~/.local/share/jank`.
[jank.sh](jank.sh) supplies its LLVM 23 library paths and GCC 14 headers;
the command above passes the example's SDL options directly to that compiler.
With a regular jank installation on PATH, replace `./jank.sh` with `jank`.
Set `JANK_HOME` to select another exported bundle.
SDL2 and pkg-config must be installed on the host; smoke checks use Xvfb/xauth.

Verified without a running container on Linux ARM64: runtime initialization,
C++ JIT, AOT compilation, native SDL rendering, synthetic Space key handling,
and captured palette/background pixels all passed. The direct-interop version
also passed a normal-mode window-title and Escape-key exit check on X11.

To provision the same local bundle on another Linux ARM64 machine:

```sh
./install-native.sh
./jank.sh check-health
HELLO_SMOKE=1 xvfb-run -a ./jank.sh -I . -L "$(pkg-config --variable=libdir sdl2)" -l SDL2 run hello.jank
python3 check-snapshot.py
```

The installer uses Docker **once** to build/export the known working toolchain,
then installs the compiler, matching LLVM libraries, and GCC headers into the
user directory. It uses the existing image if available. Building the image
needs several GB of disk and RAM and uses two compilation jobs. The bundle
retains its original Ubuntu library dependencies alongside the compiler;
normal launches execute directly on the host, with no container or chroot.
This bundle has been tested on this Arch Linux ARM64 VM, not every Linux distro.

The original all-container runner remains available as
`./run-docker-arm64.sh --smoke`.

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
  jank -I . -L /usr/lib/x86_64-linux-gnu -l SDL2 run hello.jank
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
