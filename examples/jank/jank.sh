#!/usr/bin/env bash
set -euo pipefail
if [[ -z ${JANK_HOME:-} ]] && command -v jank >/dev/null 2>&1; then
  exec jank "$@"
fi
prefix=${JANK_HOME:-$HOME/.local/share/jank}
if [[ ! -x $prefix/bin/jank ]]; then
  echo 'Install jank on PATH, or run ./install-native.sh to export the tested ARM64 toolchain.' >&2
  exit 1
fi
# The exported Ubuntu build needs its matching LLVM and GCC headers on Arch.
export PATH="$prefix/llvm-23/bin:$PATH"
export LD_LIBRARY_PATH="$prefix/llvm/lib:$prefix/llvm-23/lib${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"
export LANG=C.UTF-8 LC_ALL=C.UTF-8
export JANK_EXTRA_FLAGS="-nostdinc++ -I$prefix/gcc14/include -I$prefix/gcc14/target -I$prefix/gcc14/include/backward ${JANK_EXTRA_FLAGS:-}"
# Include options participate in jank's PCH cache key; EXTRA_FLAGS alone do not.
exec "$prefix/bin/jank" -I "$prefix/gcc14/include" -I "$prefix/gcc14/target" -I "$prefix/gcc14/include/backward" "$@"
