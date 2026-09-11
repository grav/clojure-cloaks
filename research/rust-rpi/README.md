# Raspberry Pi 4: pure Rust serial hello

A **pure Rust hardware bring-up step**, before selecting a Clojure-to-Rust
implementation. It boots without Linux, prints a greeting over the PL011 UART,
and echoes typed input. The kernel has no dependencies, allocator, or Rust `std`.
A small assembly entry point sets the stack, clears BSS and parks secondary cores.

**Verified on a physical Raspberry Pi 4 and in QEMU's `raspi4b` model.**
On 11 September 2026 the 813-byte kernel booted from SD, printed its greeting,
and passed a bidirectional UART echo check containing `Björk 🌍` and CRLF.
The user also confirmed the periodic ACT LED heartbeat.
[Physical serial capture](evidence/physical-pi4-serial.log) ·
[Tested card configuration](evidence/physical-pi4-config.txt).

QEMU additionally checks CRLF handling and UTF-8 echo. The ELF is AArch64 with
entry point `0x80000` and no undefined symbols. This kernel is not compatible
with the Pi 1.

## Wiring the Pi 4

Disconnect Pi power and unplug the adapter's USB before wiring.
Use a **3.3 V TTL UART adapter**, not 5 V TTL or an RS-232 interface.
The detected Prolific PL2303 USB identity alone does not establish output voltage.

| USB serial adapter | Pi 4 physical header pin | Pi signal |
| --- | --- | --- |
| GND | 6 | Ground |
| RX / RXD | 8 | GPIO14 / TX |
| TX / TXD | 10 | GPIO15 / RX |

These are **physical header positions**, not GPIO numbers 6, 8 and 10.
Pins 6, 8 and 10 are adjacent positions along the even-numbered header row,
counting from the pin-1 end. Follow adapter labels rather than wire colours.
Leave its VCC / 5V / 3V3 wire disconnected; power the Pi separately through USB-C.

[Official Raspberry Pi UART documentation](https://www.raspberrypi.com/documentation/computers/configuration.html#configure-uarts).

## Build and test

Docker supplies Rust 1.98.0, the `aarch64-unknown-none-softfloat` target, GNU
binutils, QEMU and a serial terminal. The first invocation builds the toolchain
image; no host package installation is needed.

```sh
cd research/rust-rpi
./build.sh
./toolchain.sh python3 smoke.py
./stage-sd.sh
```

The last command prepares `build/sdcard/` with:

- `kernel8.img`: our Rust program.
- `config.txt`: 64-bit boot and a 48 MHz UART clock for 115200 baud.
- `start4.elf`, `fixup4.dat`: Raspberry Pi's Pi 4 boot firmware.
- `bcm2711-rpi-4-b.dtb`: the default Pi 4 device tree used during firmware startup.
- `LICENCE.broadcom` and `SHA256SUMS`.

Boot firmware is pinned to Raspberry Pi firmware commit
`12eeaa12865869b07db760f4bbb7507ec6f1976c` and retains its upstream license.
The preparation script **does not open, format or write any SD card**.

The supplied 29.1 GiB card (boot UUID `20AC-1830`) was inspected and prepared
on 11 September 2026. It contains Arch Linux ARM. Its original boot partition
was copied byte-for-byte and verified against the card before modification:

`../../.cache/rust-rpi-card-backup/20AC-1830/boot-partition.img`

That directory also contains its SHA256, the original `config.txt`, and the
first MiB of the card containing its partition table. The Linux root partition
was only inspected read-only and was not modified.

For this existing card, installation added `rust-pi4.img` and
`config-before-rust.txt`, then replaced `config.txt` to select the Rust image.
The existing Pi 4 boot firmware and Linux kernel were retained. All three files
were verified by reading them back. To restore its prior boot selection, copy
`config-before-rust.txt` back to `config.txt` on the boot partition.
The installed 813-byte kernel passed physical boot and Unicode serial echo:
SHA256 `112e709883016194641c619ad5aab4d99f7abca2d0dbe17aa49799ef88285a6f`.
It flashes the Pi 4B ACT LED three times at startup, then toggles it every
half-second while waiting for input. The periodic heartbeat was observed.

The successful card configuration retains the default Pi 4 device tree and
enables firmware serial diagnostics (`uart_2ndstage=1`). Its SHA256 is
`27e8416464b3eff16014aff3dd9ed89cebfaad50a54deb36069d478ac6903a14`.
Firmware output confirms loading the DTB and relocating the kernel to `0x80000`,
followed by the Rust greeting and successful echo.
Earlier attempts with `device_tree=` produced no greeting or heartbeat.
Removing that override and enabling diagnostics together restored boot; their
individual effects have not been isolated. The default DTB setup matches the
[Rust Pi 4 tutorial](https://github.com/rust-embedded/rust-raspberrypi-OS-tutorials/tree/master/05_drivers_gpio_uart).
The physical test used the card's existing firmware and DTB; the separately
pinned firmware downloaded by `stage-sd.sh` has not been tested on this board.
The LED mapping is [GPIO42, active high](https://code.googlesource.com/linux/torvalds/linux/+/6f771ce6c3bb2c4dc9d348b578871c7adb8b285b/arch/arm/boot/dts/bcm2711-rpi-4-b.dts).

## On the real board

After inspecting and preparing the selected SD card, insert it in the Pi 4.
Connect the three serial wires, plug the adapter back into the VM, then start:

```sh
./serial.sh
# Or explicitly select a different USB serial device:
./serial.sh /dev/ttyUSB0
```

Power the Pi via USB-C. The expected console is:

```text
Hello, world from bare-metal Rust on Raspberry Pi 4!
115200 baud, 8N1. No OS, no heap.
Type a line; I will echo it.
>
```

The terminal uses **115200 baud, 8 data bits, no parity, one stop bit**, without
hardware flow control. Press Ctrl+] to exit. Boot output plus a successfully
echoed line are the physical-board acceptance checks; both passed.
To repeat the automated greeting and Unicode echo check, run
`python3 check-hardware.py` on a host with pyserial installed and access to
`/dev/ttyUSB0`, before powering on the Pi.

Later, a UART chainloader can replace repeated SD writes. The
[Rust Raspberry Pi tutorials](https://github.com/rust-embedded/rust-raspberrypi-OS-tutorials/tree/master/06_uart_chainloader)
support Pi 4. The direct-boot hardware baseline is now established.

Hardware references: [BCM2711 peripheral manual](https://datasheets.raspberrypi.com/bcm2711/bcm2711-peripherals.pdf),
[Pi 4 QEMU model](https://www.qemu.org/docs/master/system/arm/raspi.html),
[boot configuration](https://www.raspberrypi.com/documentation/computers/config_txt.html).

## Upstream contribution

[RusPiRo loader PR #8](https://github.com/RusPiRo/ruspiro-loader/pull/8)
fixes two GPIO pull-control gates that reference an undeclared `pi4` feature,
using the actual `pi4_low` and `pi4_high` features, and adds boot troubleshooting
notes. [Fork](https://github.com/grav/ruspiro-loader).
The PR is a draft: static feature checks passed, but a full RusPiRo loader build
and Pi 4 payload-transfer test remain outstanding. The successful physical test
above exercises this standalone Rust kernel, not RusPiRo's loader.
