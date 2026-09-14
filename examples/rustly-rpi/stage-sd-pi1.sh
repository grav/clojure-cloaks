#!/usr/bin/env bash
# Assemble boot files only; never opens or writes a block device.
set -euo pipefail
cd "$(dirname "$0")"
test -s build/kernel.img || ./build-pi1.sh
firmware=12eeaa12865869b07db760f4bbb7507ec6f1976c
mkdir -p build/sdcard-pi1
for name in bootcode.bin start.elf fixup.dat LICENCE.broadcom; do
  curl -fsSL "https://raw.githubusercontent.com/raspberrypi/firmware/$firmware/boot/$name" -o "build/sdcard-pi1/$name"
done
cp config-pi1.txt build/sdcard-pi1/config.txt
cp build/kernel.img build/sdcard-pi1/
(cd build/sdcard-pi1 && sha256sum config.txt kernel.img bootcode.bin start.elf fixup.dat LICENCE.broadcom > SHA256SUMS)
printf 'Prepared %s/build/sdcard-pi1; no SD card has been modified.\n' "$PWD"
