#!/usr/bin/env bash
# Export the existing, tested ARM64 build once; execution then needs no container.
set -euo pipefail
cd "$(dirname "$0")"
[[ $(uname -s) == Linux && $(uname -m) == aarch64 ]] || { echo 'This bundle is for Linux ARM64.' >&2; exit 1; }
prefix=${JANK_HOME:-$HOME/.local/share/jank}
[[ $prefix != *' '* ]] || { echo 'JANK_HOME must not contain spaces (jank splits EXTRA_FLAGS on spaces).' >&2; exit 1; }
image=clojure-hello/jank-arm64:local
if ! docker image inspect "$image" >/dev/null 2>&1; then
  docker build --platform linux/arm64 -f Dockerfile.arm64 -t "$image" .
fi
mkdir -p "$prefix"
docker run --rm --init -v "$prefix:$prefix" -e "PREFIX=$prefix" \
  -e "DEMO_UID=$(id -u)" -e "DEMO_GID=$(id -g)" "$image" bash -c '
    set -e
    cmake --install /opt/jank/compiler+runtime/build --prefix "$PREFIX"
    mkdir -p "$PREFIX/llvm/lib" "$PREFIX/gcc14/include" "$PREFIX/gcc14/target"
    cp -a /usr/lib/llvm-23/. "$PREFIX/llvm-23/"
    for name in libclang-cpp.so.23.1 libLLVM.so.23.1; do
      cp -L "/usr/lib/llvm-23/lib/$name" "$PREFIX/llvm/lib/"
    done
    for name in libedit.so.2 libz3.so.4 libxml2.so.2 libtinfo.so.6 libbsd.so.0 libicuuc.so.74 libicudata.so.74 libmd.so.0; do
      cp -L "/lib/aarch64-linux-gnu/$name" "$PREFIX/llvm/lib/"
    done
    cp -a /usr/include/c++/14/. "$PREFIX/gcc14/include/"
    cp -a /usr/include/aarch64-linux-gnu/c++/14/. "$PREFIX/gcc14/target/"
    chown -R "$DEMO_UID:$DEMO_GID" "$PREFIX"
  '
health_log=$(mktemp)
trap 'rm -f "$health_log"' EXIT
JANK_HOME="$prefix" ./jank.sh check-health 2>&1 | tee "$health_log"
if grep -q "❌" "$health_log"; then
  echo "Native jank health check failed; see diagnostics above." >&2
  exit 1
fi
