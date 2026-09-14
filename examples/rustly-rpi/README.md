# Rustly Clojure on bare-metal Raspberry Pi 1 and Pi 4

[hello.clj](hello.clj) supplies the greeting, response after each serial input
line, and LED on/off actions. Rustly transpiles those forms into Rust; Rust
compiles the generated functions and our board driver into an ARMv6 (Pi 1) or
AArch64 (Pi 4) SD-boot image. There is no JVM, interpreter, OS, allocator, or Rust `std` on the board.

**The original Rustly hello passed on the physical Pi 4. The Morse extension
passes QEMU and timing tests; physical Pi 4 boot and the `clj` serial command now pass. Visual LED verification remains pending.**
[Physical Morse capture](evidence/physical-morse-serial.log) ·
[Physical hello capture](evidence/physical-hello-serial.log) ·
[Generated Rust](evidence/generated-app.rs) · [Morse QEMU transcript](evidence/qemu-transcript.txt).
The Pi 4 image boots at `0x80000`. The new **original Pi 1 A/B (26-pin header)**
image boots at `0x8000`; its build, QEMU serial checks, and physical Pi 1 boot
and `clj` serial acknowledgement pass. Physical LED timing verification is pending.
[Physical Pi 1 capture](evidence/physical-pi1-serial.log). These boards use separate kernel images.
Both builds have no unresolved symbols.

## Original Raspberry Pi 1 A/B

From the repository root:

```sh
cd examples/rustly-rpi
./build-pi1.sh
./test.sh pi1
./stage-sd-pi1.sh
```

This produces `build/kernel.img` (4,224 bytes) and assembles
`build/sdcard-pi1/` with `bootcode.bin`, `start.elf`, `fixup.dat`, `config.txt`,
and checksums. Staging does not write an SD card. Rebuild after source edits
before staging. The configuration is for the original A/B, not the A+/B+.

The Clojure Morse alphabet/messages and Rust sequencer are shared with Pi 4.
The Pi 1 implementation changes:

- ARM1176/ARMv6 startup in 32-bit ARM mode, with code and stack at `0x8000`.
- BCM2835 peripherals at `0x20000000`, including the older GPIO pull-control sequence.
- The 1 MHz system timer instead of the ARM64 architectural counter.
- GPIO16 ACT LED, active low, instead of the Pi 4's active-high GPIO42.

Rust's [`armv6-none-eabi`](https://doc.rust-lang.org/rustc/platform-support/armv6-none-eabi.html)
target has no prebuilt `core`. The separate Docker toolchain installs `rust-src`
for Rust 1.98.0 and uses `RUSTC_BOOTSTRAP=1` only for Cargo's unstable `build-std`
step, building `core` and `compiler_builtins` for ARM1176. The firmware remains
`no_std`, allocation-free, and soft-float.

QEMU's `raspi1ap` machine supplies the same ARM1176/BCM2835 peripherals for the
serial tests. It is an A+ model, so this does **not** verify the original board's
GPIO16 LED wiring. The test uses `-bios build/kernel.img` to load the actual raw
image at `0x8000`; QEMU's normal `-kernel` path uses different loading rules.
[Pi 1 QEMU transcript](evidence/qemu-pi1-transcript.txt).

The serial wiring remains ground on physical pin 6, adapter RX on pin 8, and
adapter TX on pin 10; use separate **micro-USB power** for Pi 1. On hardware,
expect three short ACT flashes, then an idle LED until a serial message arrives.
Use `./serial.sh` at 115200 baud; `clj` plus Enter should repeat its Morse pattern.

The 14.8 GiB card with boot UUID `3DDC-D542` has been prepared for the Pi 1.
Its existing Arch Linux ARM root partition and original kernel/firmware are
preserved. The new `rustly-pi1.img` and `config.txt` were readback-verified;
restore `config-before-rustly-pi1.txt` as `config.txt` to boot the original Linux
installation again. A byte-for-byte backup of the original 100 MiB boot partition
is stored locally at `.cache/rustly-pi1-card-backup/3DDC-D542/boot-partition.img`
(relative to the repository root). Physical Pi 1 boot and bidirectional serial communication passed on 14 September
2026: the firmware printed its Rustly greeting and acknowledged `clj` with
`Repeating Morse: clj`. Visual LED timing verification is still pending.

Installed Pi 1 kernel SHA256:
`1617fe751bce9e23497320b9c60b7efa9a00feecf1a3ed4ac81214e17a8c8b78`.

Hardware references: [BCM2835 processor](https://www.raspberrypi.com/documentation/hardware/raspberrypi/bcm2835/),
[Circle's board-specific ACT LED table](https://github.com/rsta2/circle/blob/master/lib/machineinfo.cpp),
and [Raspberry Pi firmware boot settings](https://www.raspberrypi.com/documentation/computers/legacy_config_txt.html).

## Serial Morse

Send `clj` followed by Enter to repeat **`-.-. .-.. .---`** on the green ACT LED.
A dot lasts 200 ms, a dash 600 ms. Gaps are one unit between elements, three
between letters, and seven between words or repetitions. Letters are case
insensitive; A-Z, 0-9, and spaces are supported, up to 64 input bytes.

A new line replaces the message while blinking. An empty or all-space line
stops it and turns the LED off. Invalid characters or an overlong line leave
the previous message unchanged. Backspace edits the input before submission;
after overflow, submit the line to discard it and start again. CRLF counts as
one submission. Before the first message the LED stays off after its startup
flashes.

The Morse alphabet and status messages are authored in `hello.clj`. The
allocation-free Rust sequencer polls the timer and UART without sleeping through
pulses, so input remains responsive. Timing tests cover `clj` and repetition,
word gaps, replacement, stopping, invalid input, capacity, and counter wrap.
QEMU checks command acknowledgement, replacement, CRLF, Unicode rejection,
overlong input, backspace, and stop. Physical pulse timing is not yet verified.

## Pi 4 build and run

Install the Clojure CLI, Java, and Docker. Rust and QEMU are supplied by the same
Docker toolchain as the pure Rust baseline (Rust 1.98.0, target
`aarch64-unknown-none-softfloat`). Maven dependencies are pinned in `deps.edn`.

```sh
cd examples/rustly-rpi
./build.sh
./test.sh
./stage-sd.sh
```

`build.sh` regenerates `build/app.rs` from `hello.clj` every time, compiles it,
and writes `build/kernel8.img`. `stage-sd.sh` assembles a directory of firmware
files; it does not write or format an SD card. Run `build.sh` after source edits
before staging. The downloaded firmware is distinct from the existing card's
firmware used for the verified pure Rust baseline.

Use the [baseline wiring](../../research/rust-rpi/#wiring-the-pi-4): ground on physical pin 6,
adapter RX on pin 8, adapter TX on pin 10, and separate USB-C power.
Start the automatic hardware check before powering on the Pi:

```sh
./check-hardware.sh
# Or use an interactive serial console:
./serial.sh
```

The check waits for the Rustly-specific banner and prompt before sending test
input; it will not mistake the pure Rust baseline for this demo. It saves the
capture in `build/physical-serial.log`.

### Previously verified Pi 4 card

The prepared configuration for the existing backed-up card selects
`rustly-pi4.img`, retains the Pi 4 DTB, and enables firmware serial diagnostics.
The original 893-byte hello image and configuration were installed on card
`20AC-1830`, readback-verified, and passed physical serial verification. The
previously verified 3904-byte Morse image was installed and byte-for-byte readback-verified.
The shared source now uses a board-neutral banner, so rebuilding produces a
different hash; the hashes below identify the older physically verified images.
The previous Rustly hello is preserved as `rustly-hello.img`.
Morse kernel SHA256: `41a0c946a75f279effe7b60f5d43fca84f84064d95435b9aefce631945040771`.
Copy
`config-pure-rust.txt` back to `config.txt` to restore the working Rust baseline,
or `config-before-rust.txt` to restore the original Linux boot selection.

Original hello kernel SHA256:
`cb7253a7604b1ccfa7c0ca46fdec64f05721fe7dc3b8568a863bab521261068c`.
Installed configuration SHA256:
`ac811b00ff028ff8efde9e817853ed95719196f58da4d781c7fdbdc2084380e1`.

## Language and hardware boundary

[Rustly](https://github.com/timothypratley/rustly) is an alpha Clojure-to-Rust
transpiler supporting a small subset, not full Clojure. It uses named `(fn ...)`
forms here. The parser, translator, and emitter are vendored unchanged at commit
`e2333ee3873a33cb9e7f4a9d3e4ee849cae8cd09`; provenance and license are in
[vendor/rustly](vendor/rustly/UPSTREAM.md).

Our small `transpile.clj` calls those upstream components directly. It omits the
upstream CLI wrapper's unconditional `rpds` collection import because this
program uses no collections. No generated Rust is manually rewritten.

The Clojure source controls messages, the Morse alphabet, and LED actions. Rust supplies startup,
GPIO/PL011 register access, Morse sequencing, input validation/polling, echo, and dispatch to generated
functions. Strings in this demo's Clojure source are ASCII because Rustly emits
Rust byte-string literals; the UART driver nevertheless echoes arbitrary UTF-8
input. Collections, closures, dynamic evaluation, and general Clojure semantics
are outside this example's scope.
