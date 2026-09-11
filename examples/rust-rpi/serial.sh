#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
device=${1:-/dev/serial/by-id/usb-Prolific_Technology_Inc._USB-Serial_Controller-if00-port0}
device=$(realpath "$device")
test -c "$device"
docker run --rm --init -it --device="$device:/dev/ttyUSB0" \
  clojure-hello/rust-rpi:local python3 -m serial.tools.miniterm /dev/ttyUSB0 115200 --raw
