#!/usr/bin/env python3
"""Check the Jolt result against independent fast-doubling Fibonacci."""
from pathlib import Path
import subprocess
import os
os.chdir(Path(__file__).parent)

def pair(n):
    if n == 0:
        return 0, 1
    a, b = pair(n // 2)
    c, d = a * (2*b - a), a*a + b*b
    return (d, c+d) if n % 2 else (c, d)

Path('evidence').mkdir(exist_ok=True)
for n in (0, 1, 2, 20, 20000):
    result = subprocess.run(['./run.sh', str(n)], text=True, capture_output=True, timeout=180)
    assert result.returncode == 0, result.stderr + result.stdout
    values = dict(line.split(': ', 1) for line in result.stdout.splitlines() if ': ' in line)
    expected = pair(n)[0]
    digits = str(expected)
    assert values['Naive Fibonacci(20)'] == '6765', values
    assert values['Tail-call Fibonacci(20)'] == '6765', values
    assert values['digits'] == str(len(digits)), values
    assert values['first'] == digits[:30], values
    assert values['last'] == digits[-30:], values
    assert values['mod-1000000007'] == str(expected % 1000000007), values
    if n == 20000:
        Path('evidence/jolt.txt').write_text(result.stdout)
    print(f'PASS: Jolt Fibonacci({n}) matches independent fast-doubling reference')
result = subprocess.run(['clojure', '-J-Xss256k', '-M', 'compare-jvm.clj'], text=True, capture_output=True, timeout=60)
assert result.returncode == 0 and 'StackOverflowError' in result.stdout, result.stderr + result.stdout
Path('evidence/jvm.txt').write_text(result.stdout)
print(result.stdout.strip())
