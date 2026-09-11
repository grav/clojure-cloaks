#![no_std]
#![no_main]

use core::arch::{asm, global_asm};
use core::panic::PanicInfo;
use core::ptr::{read_volatile, write_volatile};

global_asm!(include_str!("boot.S"));
mod morse;

// BCM2711 (Pi 4) physical peripheral addresses; MMU remains disabled.
const GPIO: usize = 0xfe20_0000;
const UART: usize = 0xfe20_1000;

unsafe fn read(address: usize) -> u32 {
    read_volatile(address as *const u32)
}

unsafe fn write(address: usize, value: u32) {
    write_volatile(address as *mut u32, value);
}

unsafe fn init_uart() {
    write(UART + 0x30, 0); // UARTCR: disable before configuring.
    // GPIO14/15 -> ALT0 (PL011 TX/RX), preserving all other pin functions.
    let select = read(GPIO + 0x04);
    write(GPIO + 0x04, (select & !((7 << 12) | (7 << 15))) | (4 << 12) | (4 << 15));
    // Pi 4 uses GPPUPPDN0, unlike the older Pi pull-control sequence.
    write(GPIO + 0xe4, read(GPIO + 0xe4) & !((3 << 28) | (3 << 30)));
    write(UART + 0x44, 0x7ff); // Clear pending interrupts.
    write(UART + 0x38, 0); // Polling only; mask interrupts.
    // 48 MHz UART clock / (16 * 115200) = 26 + 3/64.
    // config.txt fixes the real board's UART clock at 48 MHz.
    write(UART + 0x24, 26);
    write(UART + 0x28, 3);
    write(UART + 0x2c, (3 << 5) | (1 << 4)); // 8N1, FIFO enabled.
    write(UART + 0x30, (1 << 9) | (1 << 8) | 1); // RX, TX, UART enabled.
    asm!("dsb sy", "isb", options(nostack, preserves_flags));
}

fn put(byte: u8) {
    unsafe {
        while read(UART + 0x18) & (1 << 5) != 0 {
            core::hint::spin_loop();
        }
        write(UART, byte as u32);
    }
}

fn print(text: &str) {
    for byte in text.bytes() {
        if byte == b'\n' {
            put(b'\r');
        }
        put(byte);
    }
}

fn ticks() -> u64 {
    let value: u64;
    unsafe { asm!("mrs {}, cntpct_el0", out(reg) value, options(nomem, nostack)) };
    value
}

fn timer_frequency() -> u64 {
    let value: u64;
    unsafe { asm!("mrs {}, cntfrq_el0", out(reg) value, options(nomem, nostack)) };
    value
}

fn led(on: bool) {
    // Pi 4B ACT LED: GPIO42, active high (bank 1, bit 10).
    unsafe { write(GPIO + if on { 0x20 } else { 0x2c }, 1 << 10) };
}

fn startup_flashes(period: u64) {
    unsafe {
        // GPIO42 output; preserve the functions of the other pins.
        write(GPIO + 0x10, (read(GPIO + 0x10) & !(7 << 6)) | (1 << 6));
    }
    for _ in 0..3 {
        led(true);
        let start = ticks();
        while ticks().wrapping_sub(start) < period { core::hint::spin_loop(); }
        led(false);
        let start = ticks();
        while ticks().wrapping_sub(start) < period { core::hint::spin_loop(); }
    }
}

fn try_get() -> Option<u8> {
    unsafe {
        if read(UART + 0x18) & (1 << 4) != 0 {
            return None;
        }
        let data = read(UART);
        if data & 0xf00 == 0 {
            Some(data as u8)
        } else {
            write(UART + 0x04, 0);
            None
        }
    }
}

#[no_mangle]
pub extern "C" fn kernel_main() -> ! {
    let frequency = timer_frequency();
    startup_flashes(frequency / 10);
    unsafe { init_uart() };
    greeting();
    configure_morse();
    // Only core 0 runs; initialization is complete before taking this copy.
    let alphabet = unsafe { ALPHABET };
    let mut player = morse::Morse::new();
    let mut line = [0u8; morse::MAX_INPUT];
    let mut length = 0;
    let mut overflow = false;
    let mut previous_cr = false;
    let mut led_on = false;
    let unit = (frequency / 5).max(1); // 200 ms dot; dash 600 ms.
    loop {
        let on = player.output(ticks(), unit);
        if on != led_on {
            if on { heartbeat_on(); } else { heartbeat_off(); }
            led_on = on;
        }
        let Some(byte) = try_get() else { continue };
        match byte {
            b'\n' if previous_cr => {},
            b'\r' | b'\n' => {
                if overflow || player.set(&line[..length], &alphabet, ticks()).is_err() {
                    invalid();
                } else if line[..length].iter().all(|b| *b == b' ') {
                    stopped();
                } else {
                    accepted();
                    uart_write(&line[..length]);
                    prompt();
                }
                length = 0;
                overflow = false;
            },
            8 | 127 => {
                if length > 0 && !overflow { length -= 1; print("\u{8} \u{8}"); }
            },
            _ => {
                put(byte);
                if length < line.len() { line[length] = byte; length += 1; }
                else { overflow = true; }
            },
        }
        previous_cr = byte == b'\r';
    }
}

#[panic_handler]
fn panic(_: &PanicInfo) -> ! {
    print("\nPANIC\n");
    loop {
        unsafe { asm!("wfe", options(nomem, nostack)) };
    }
}

fn uart_write(bytes: &[u8]) {
    for &byte in bytes {
        if byte == b'\n' { put(b'\r'); }
        put(byte);
    }
}

include!(concat!(env!("CARGO_MANIFEST_DIR"), "/build/app.rs"));

fn board_led_on() { led(true); }
fn board_led_off() { led(false); }

// Written only during single-core startup by the Rustly-generated configuration.
static mut ALPHABET: [&'static [u8]; 36] = [b""; 36];
fn register_morse(index: usize, code: &'static [u8]) {
    unsafe { ALPHABET[index] = code; }
}
