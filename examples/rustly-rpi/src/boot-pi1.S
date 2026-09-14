.syntax unified
.cpu arm1176jzf-s
.arm
.section .text.boot
.global _start
_start:
    // Firmware enters in privileged ARM mode with the MMU disabled.
    cpsid if
    ldr sp, =0x8000
    ldr r0, =__bss_start
    ldr r1, =__bss_end
    mov r2, #0
1:
    cmp r0, r1
    strlo r2, [r0], #4
    blo 1b
    bl kernel_main
2:
    b 2b
