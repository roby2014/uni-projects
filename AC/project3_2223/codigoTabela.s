movt r0, #0xFD
strb r1, [r0, #0]
push r1
lsl, r1, r1, #3
ldr r3, [r2, #0]
pop r4