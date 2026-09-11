#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
device=${1:-/dev/serial/by-id/usb-Prolific_Technology_Inc._USB-Serial_Controller-if00-port0}
device=$(realpath "$device")
test -c "$device"
mkdir -p build
set -o pipefail
docker run --rm --init --device="$device:/dev/ttyUSB0" -v "$PWD:/app:ro" \
  clojure-hello/rust-rpi:local python3 -u /app/check-hardware.py --timeout 600 \
  | tee build/physical-serial.log
