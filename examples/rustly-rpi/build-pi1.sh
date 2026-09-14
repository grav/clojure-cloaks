#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
clojure -M transpile.clj
RPI_BOARD=pi1 ./toolchain.sh bash -c '
  set -e
  # Tier-3 ARMv6 has no prebuilt core. Build it from Rust 1.98.0 rust-src.
  RUSTC_BOOTSTRAP=1 cargo build -Z build-std=core,compiler_builtins \
    -Z build-std-features=compiler-builtins-mem --target armv6-none-eabi --release
  elf=target/armv6-none-eabi/release/hello-rustly-pi4
  arm-none-eabi-objcopy -O binary "$elf" build/kernel.img
  arm-none-eabi-readelf -h "$elf"
  test -z "$(arm-none-eabi-nm -u "$elf")"
  wc -c build/kernel.img
'
