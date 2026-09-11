#!/usr/bin/env python3
"""Boot the actual Pi 4 kernel image and verify its PL011 serial console."""
import os
import selectors
import subprocess
import time

proc = subprocess.Popen([
    'qemu-system-aarch64', '-M', 'raspi4b', '-kernel', 'build/kernel8.img',
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
    boot = receive(b'> ')
    assert boot.count(b'Hello, world from bare-metal Rust on Raspberry Pi 4!') == 1
    assert b'No OS, no heap.' in boot
    proc.stdin.write(b'Hello from the VM\r\n')
    proc.stdin.flush()
    response = receive(b'> ')
    assert response == b'Hello from the VM\r\n> ', response
    proc.stdin.write('Björk 🌍\r'.encode())
    proc.stdin.flush()
    response = receive(b'> ')
    assert response == 'Björk 🌍\r\n> '.encode(), response
    print(transcript.decode())
    print('PASS: Pi 4 boot, one core greeting, UART RX/TX, CRLF and UTF-8 echo')
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
