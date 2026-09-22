#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ $(uname -s) == Darwin ]]; then
  if [[ $# -gt 0 ]]; then
    device=$1
  else
    shopt -s nullglob
    devices=(/dev/cu.usbserial* /dev/cu.usbmodem* /dev/cu.SLAB_USBtoUART* /dev/cu.wchusbserial*)
    if [[ ${#devices[@]} -ne 1 ]]; then
      printf 'Expected one USB serial adapter; found %s.\n' "${#devices[@]}" >&2
      if [[ ${#devices[@]} -gt 0 ]]; then
        printf '  %s\n' "${devices[@]}" >&2
      fi
      printf 'Usage: %s /dev/cu.<device>\n' "$0" >&2
      exit 1
    fi
    device=${devices[0]}
  fi
  if [[ ! -c "$device" ]]; then
    printf 'Not a serial device: %s\n' "$device" >&2
    exit 1
  fi
  printf 'Connecting to %s at 115200 baud. Quit with Ctrl-A, then K, then Y.\n' "$device"
  exec screen "$device" 115200
fi

device=${1:-/dev/serial/by-id/usb-Prolific_Technology_Inc._USB-Serial_Controller-if00-port0}
device=$(realpath "$device")
test -c "$device"
docker run --rm --init -it --device="$device:/dev/ttyUSB0" \
  clojure-hello/rust-rpi:local python3 -m serial.tools.miniterm /dev/ttyUSB0 115200 --raw
