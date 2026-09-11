.section .text.boot
.global _start
_start:
    // Only core 0 enters Rust. Leave the other cores parked.
    mrs x0, mpidr_el1
    and x0, x0, #3
    cbnz x0, 2f
    msr daifset, #0xf
    // A private boot stack grows down below the firmware load address.
    ldr x0, =0x80000
    mov sp, x0
    ldr x0, =__bss_start
    ldr x1, =__bss_end
1:
    cmp x0, x1
    b.hs 3f
    str xzr, [x0], #8
    b 1b
2:
    wfe
    b 2b
3:
    bl kernel_main
    b 2b
