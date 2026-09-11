#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
jank_root=${JANK_SOURCE:-/opt/jank/compiler+runtime}
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
clang++-23 -std=c++17 \
  -I"$jank_root/third-party/cpptrace/include" \
  -I"$jank_root/build/third-party/cpptrace/include" check.cpp \
  "$jank_root/build/third-party/cpptrace/libcpptrace.a" \
  "$jank_root/build/_deps/libdwarf-build/src/lib/libdwarf/libdwarf.a" \
  -lz -lzstd -ldl -o "$work/check"
for target in aarch64-unknown-linux-gnu x86_64-unknown-linux-gnu; do
  clang++-23 --target="$target" -g -gdwarf-4 -O0 -c fixture.cpp -o "$work/fixture.o"
  llvm-objcopy-23 --change-section-address .text=0x100000 "$work/fixture.o" "$work/loaded.o"
  echo "Checking $target JIT debug information"
  "$work/check" "$work/loaded.o"
done
