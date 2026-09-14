#!/usr/bin/env python3
"""Wait for our kernel banner, then verify echo; never send to an unknown console."""
import argparse
import time
import serial

parser = argparse.ArgumentParser()
parser.add_argument('--timeout', type=float, default=300)
parser.add_argument('--port', default='/dev/ttyUSB0')
args = parser.parse_args()
uart = serial.Serial(port=None, baudrate=115200, timeout=0.5)
uart.dtr = False
uart.rts = False
uart.port = args.port
uart.open()
print('Listening at 115200 8N1; waiting for the Rust kernel banner.', flush=True)
received = bytearray()
deadline = time.monotonic() + args.timeout
banners = (b'Hello, world from Rustly Clojure on Raspberry Pi!',
           b'Hello, world from Rustly Clojure on Raspberry Pi 4!')
try:
    while time.monotonic() < deadline:
        chunk = uart.read(4096)
        if chunk:
            received.extend(chunk)
            print(repr(chunk), flush=True)
            if any(banner in received for banner in banners) and received.endswith(b'\r\n> '):
                break
        if len(received) > 65536:
            del received[:-65536]
    else:
        raise SystemExit('No complete Rust greeting/prompt received before timeout; no test data sent.')
    uart.write(b'clj\r\n')
    uart.flush()
    expected = b'clj\r\nRepeating Morse: clj\r\n> '
    reply = bytearray()
    deadline = time.monotonic() + 10
    while time.monotonic() < deadline and expected not in reply:
        reply.extend(uart.read(4096))
    print('Echo:', repr(bytes(reply)), flush=True)
    if expected not in reply:
        raise SystemExit('Rust boot verified, but serial echo check failed.')
    print('PASS: physical Raspberry Pi Rustly Clojure boot and serial Morse command accepted; observe LED timing separately.', flush=True)
finally:
    uart.close()
