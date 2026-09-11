#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
cache=${JOLT_DEMO_CACHE:-${XDG_CACHE_HOME:-$HOME/.cache}/clojure-dialects/jolt}
rev=$(cat JOLT_REV)
chez_src="$cache/chez-src-10.4.1"
chez_prefix="$cache/chez-10.4.1"
jolt_src="$cache/jolt-$rev"
mkdir -p "$cache"
if [[ ! -x "$chez_prefix/bin/scheme" ]]; then
  if [[ ! -d "$chez_src/.git" ]]; then
    git clone --depth 1 --branch v10.4.1 --recurse-submodules --shallow-submodules https://github.com/cisco/ChezScheme.git "$chez_src"
  fi
  (cd "$chez_src"; ./configure --threads --installprefix="$chez_prefix"; make -j4; make install)
fi
if [[ ! -d "$jolt_src/.git" ]]; then
  git clone --no-checkout https://github.com/jolt-lang/jolt.git "$jolt_src"
  git -C "$jolt_src" checkout "$rev"
  git -C "$jolt_src" submodule update --init --recursive --depth 1
fi
JOLT_CHEZ="$chez_prefix/bin/scheme" "$jolt_src/bin/jolt" -e '(println "Native Jolt ready")'

# Put Chez where the upstream launcher already looks for local installations.
mkdir -p "$jolt_src/.cache/local"
chez_link="$jolt_src/.cache/local/chezscheme-10.4.1"
if [[ ! -e "$chez_link" && ! -L "$chez_link" ]]; then ln -s "$chez_prefix" "$chez_link"; fi

# Expose Jolt's upstream launcher directly, without a demo wrapper.
mkdir -p "$HOME/.local/bin"
link="$HOME/.local/bin/jolt"
if [[ ! -e "$link" && ! -L "$link" ]]; then ln -s "$jolt_src/bin/jolt" "$link"; fi
echo 'Add ~/.local/bin to PATH, then run: jolt fib.clj'
