#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./toolchain.sh bash -c '
  set -e
  cargo build --release
  mkdir -p build
  aarch64-linux-gnu-objcopy -O binary target/aarch64-unknown-none-softfloat/release/hello-rust-pi4 build/kernel8.img
  aarch64-linux-gnu-readelf -h target/aarch64-unknown-none-softfloat/release/hello-rust-pi4
  test -z "$(aarch64-linux-gnu-nm -u target/aarch64-unknown-none-softfloat/release/hello-rust-pi4)"
  wc -c build/kernel8.img
'
