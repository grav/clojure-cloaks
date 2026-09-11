#!/usr/bin/env python3
"""Check the SDL smoke frame and write a PNG using only the standard library."""
import struct
import zlib
from pathlib import Path

bmp = Path('hello.bmp').read_bytes()
offset = struct.unpack_from('<I', bmp, 10)[0]
width, height = struct.unpack_from('<ii', bmp, 18)
bits = struct.unpack_from('<H', bmp, 28)[0]
assert (width, height, bits) == (640, 400, 32)

def rgb(x, y):
    start = offset + ((height - 1 - y) * width + x) * 4
    return bmp[start:start + 3][::-1]

assert rgb(230, 110) == bytes((205, 100, 65)), 'Space must select the second palette colour'
assert rgb(0, 0) == bytes((246, 242, 231)), 'Background must be rendered'

def chunk(kind, data):
    return struct.pack('>I', len(data)) + kind + data + struct.pack('>I', zlib.crc32(kind + data))

pixels = b''.join(b'\x00' + b''.join(rgb(x, y) for x in range(width)) for y in range(height))
Path('screenshot.png').write_bytes(
    b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0))
    + chunk(b'IDAT', zlib.compress(pixels)) + chunk(b'IEND', b''))
print('Native SDL rendering and Space-key palette change verified; screenshot.png written.')
