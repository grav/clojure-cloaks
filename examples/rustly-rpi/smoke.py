#!/usr/bin/env python3
"""Boot a kernel image in QEMU and verify its PL011 serial console."""
import argparse
import os
from pathlib import Path
import selectors
import subprocess
import time

parser = argparse.ArgumentParser()
parser.add_argument('--pi1', action='store_true')
args = parser.parse_args()
emulator, machine, kernel = (
    ('qemu-system-arm', 'raspi1ap', 'build/kernel.img') if args.pi1 else
    ('qemu-system-aarch64', 'raspi4b', 'build/kernel8.img'))
proc = subprocess.Popen([
    emulator, '-M', machine, '-bios' if args.pi1 else '-kernel', kernel,
    '-display', 'none', '-serial', 'stdio', '-monitor', 'none', '-no-reboot',
], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
selector = selectors.DefaultSelector()
selector.register(proc.stdout, selectors.EVENT_READ)
transcript = bytearray()

def receive(expected):
    output = bytearray()
    deadline = time.monotonic() + 20
    while expected not in output:
        if time.monotonic() >= deadline or proc.poll() is not None:
            raise AssertionError(f'Waiting for {expected!r}; received {bytes(output)!r}; QEMU status {proc.poll()}')
        for key, _ in selector.select(0.2):
            chunk = os.read(key.fileobj.fileno(), 4096)
            output.extend(chunk)
            transcript.extend(chunk)
    return bytes(output)

try:
    boot = receive(b'\r\n> ')
    assert boot.count(b'Hello, world from Rustly Clojure on Raspberry Pi!') == 1
    assert b'No OS, no heap.' in boot
    def submit(data, expected):
        proc.stdin.write(data)
        proc.stdin.flush()
        response = receive(b'\r\n> ')
        assert response == expected, response
    submit(b'clj\r\n', b'clj\r\nRepeating Morse: clj\r\n> ')
    # Replacement while the previous message is blinking.
    submit(b'SOS 42\r', b'SOS 42\r\nRepeating Morse: SOS 42\r\n> ')
    error = b'\r\nUse up to 64 ASCII letters, digits or spaces; message unchanged.\r\n> '
    submit('Björk 🌍\r'.encode(), 'Björk 🌍'.encode() + error)
    submit(b'a'*65 + b'\r', b'a'*65 + error)
    submit(b'clx\x7fj\r', b'clx\x08 \x08j\r\nRepeating Morse: clj\r\n> ')
    submit(b'\r', b'\r\nMorse stopped.\r\n> ')
    Path('build/qemu-pi1-transcript.txt' if args.pi1 else 'build/qemu-transcript.txt').write_text(transcript.decode())
    print(transcript.decode())
    print('PASS: Rustly boot, Morse requests, replacement, CRLF, invalid/long input, backspace and stop')
finally:
    selector.close()
    proc.terminate()
    try:
        proc.wait(timeout=5)
    except subprocess.TimeoutExpired:
        proc.kill()
        proc.wait()
    errors = proc.stderr.read().decode()
    if errors:
        print(errors, end='')
