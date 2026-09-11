#!/usr/bin/env python3
"""Exercise the CLI and check that the final program traverses both interpreters."""
from pathlib import Path
import re
import subprocess

HERE = Path(__file__).resolve().parent
SOURCE = """
(defn fact [n] (if (zero? n) 1 (* n (fact (dec n)))))
(defmacro twice [x] (list '+ x x))
(defprotocol P (value [x]))
(defrecord R [x] P (value [_] x))
(def ^:dynamic *x* 1)
[(str "hello" " world")
 ((let [x 40] (fn [y] (+ x y))) 2)
 (fact 5)
 (loop [n 10 sum 0] (if (zero? n) sum (recur (dec n) (+ sum n))))
 (twice 21)
 (value (->R 42))
 [(binding [*x* 42] *x*) *x*]]
"""
EXPECTED = '["hello world" 42 120 55 42 42 [42 1]]'

for depth in range(3):
    result = subprocess.run(
        ["clj", "-M", "tower.clj", "-n", str(depth), SOURCE],
        cwd=HERE, text=True, capture_output=True, timeout=60,
    )
    assert result.returncode == 0, result.stderr
    assert result.stdout.strip() == EXPECTED, result.stdout
    ready = re.findall(r"Layer (\d+) ready", result.stderr)
    assert ready == [str(n) for n in range(1, depth + 1)], ready
    counts = re.search(r"Interpreted node calls by layer: \{(.*)\}", result.stderr)
    assert counts, result.stderr
    counts = {int(k): int(v) for k, v in re.findall(r"(\d+) (\d+)", counts[1])}
    assert all(counts.get(n, 0) > 0 for n in range(1, depth + 1)), counts
    if depth == 2:
        (HERE / "evidence/tower.txt").write_text(
            "Command: clj -M tower.clj -n 2 <test expression from test-tower.py>\n"
            + result.stderr + "Result:\n" + result.stdout
        )
    print(f"PASS: depth {depth}, program result and layer traversal")

for args in ([], ["-n", "-1", "1"], ["-n", "nope", "1"], ["-n", "2"]):
    result = subprocess.run(
        ["clj", "-M", "tower.clj", *args],
        cwd=HERE, text=True, capture_output=True, timeout=30,
    )
    assert result.returncode != 0 and "Usage:" in result.stderr, result.stderr
    assert not result.stdout, result.stdout
print("PASS: missing and invalid arguments are rejected")

result = subprocess.run(
    ["clj", "-M", "tower.clj", "-n", "2", '(throw (ex-info "user-error" {}))'],
    cwd=HERE, text=True, capture_output=True, timeout=60,
)
assert result.returncode != 0 and "user-error" in result.stderr, result.stderr
assert not result.stdout, result.stdout
print("PASS: user errors fail the command")
