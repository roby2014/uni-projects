    .text
    ldr r0, var2_addr
loop:
    ldrb r1, [r0, #0]
    add r1, r1, #1
    strb r1, [r0, #0]
    b loop
var2_addr:
    .word var2

    .data
var1:
    .byte 0xAC
var2:
    .byte 126
var3:
    .word 0x2022
