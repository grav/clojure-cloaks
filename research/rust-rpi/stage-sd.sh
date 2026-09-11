#!/usr/bin/env bash
# Prepare a directory of boot files. This never opens or writes a block device.
set -euo pipefail
cd "$(dirname "$0")"
test -s build/kernel8.img || ./build.sh
firmware=12eeaa12865869b07db760f4bbb7507ec6f1976c
mkdir -p build/sdcard
for name in start4.elf fixup4.dat bcm2711-rpi-4-b.dtb LICENCE.broadcom; do
  curl -fsSL "https://raw.githubusercontent.com/raspberrypi/firmware/$firmware/boot/$name" -o "build/sdcard/$name"
done
cp config.txt build/kernel8.img build/sdcard/
(cd build/sdcard && sha256sum config.txt kernel8.img start4.elf fixup4.dat bcm2711-rpi-4-b.dtb LICENCE.broadcom > SHA256SUMS)
printf 'Prepared %s/build/sdcard; no SD card has been modified.\n' "$PWD"
