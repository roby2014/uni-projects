; AC TP 03
; a49418
; a49506
; 49447


; Considerando a solução apresentada na pergunta 2.3 para a nova configuração do sistema, imple-
; mente, em linguagem assembly do P16, um programa que anexa nos oito bits do porto de saída o valor
; presente nos bits 7 a 1 do sinal I, entendido como um número inteiro sem sinal. O valor apresentado
; no porto de saída é atualizado sempre que é detetada uma transição descendente no bit 0 do sinal I.


; ---------------------------------------------
; constants
    .equ	STACK_SIZE, 64
	.equ	INPORT_ADDRESS, 0xa000
	.equ	OUTPORT_ADDRESS, 0xb000


; ---------------------------------------------
; startup - startup code
	.section .startup
	b	_start
	b	.

_start:
	ldr	sp, top_of_stack_addr
	ldr	pc, main_addr

top_of_stack_addr:
	.word	top_of_stack

main_addr:
	.word	main


; ---------------------------------------------
; code
    .text

; void main()
main:
	push lr

main_loop:
	bl inport_read		; r0 = I
	ldr r1, I_last_addr	; r1 = &I_last

	mov r2, #0x1	 	; r2 = (mask bit 0) 
	and r2, r0, r2 		; r2 = I & 0b0000_0001 (I_last[0])
	str r2, [r1, #0]	; I_last = r2

	ldr r0, [r1, #0] 	; r0 = I_last

	mov r3, #0			; r3 = 0
	cmp r2, r3			; if (I_last == 0)
	beq main_loop		; continue

	ldr r1, I_last_addr	; r1 = &I
	ldr r1, [r1, #0] 	; r1 = I
	mov r2, #1			; r2 = 0b0000_0001
	and r2, r1, r2		; r2 = I & 1

	mov r3, #1
	cmp r2, r3			; if ((I & 1) == 1)
	beq main_loop		; continue

	mov r0, r1			; r0 = r1 = I
	bl update_value

	b main_loop

	pop pc


; void update_value(uint16_t I)
; description: updates outport value (I[7..1])
; params: r0 (I)
; effects: r1
update_value:
	push lr
	push r4
	mov r4, r0 ; save I in r4

	; send to outputport
	mov r1, #0xFE	; r1 = 0bb1111_1110 (mask bit 7 to 1)
	and r0, r0, r1	; r0 = I & 0bb1111_1110 (I[7..1])
	lsr r0, r0, #1	; r0 = r0 >> 1 (no signal value)
	bl outport_write

	; save i_last
	ldr r1, I_last_addr	; r1 = &I_last
	mov r2, #0x1	 	; r2 = (mask bit 0) 
	and r2, r4, r2 		; r2 = I & 0b0000_0001 (I_last[0])
	str r2, [r1, #0]	; I_last = r2

	pop pc

I_addr:
	.word I

; last bit 0 of I
I_last_addr:
	.word I_last


; void outport_write(uint8_t value)
; description: writes [value] into output port
; params: r0 (value)
; effects: r1 = &outport_addr
outport_write:
	ldr r1, outport_addr
	strb r0, [r1, #0]
	mov pc, lr

outport_addr:
	.word	OUTPORT_ADDRESS


; uint8_t inport_read()
; description: returns input port value
; effects: r1 = &outport_addr
; return value in r0
inport_read:
	ldr	r1, inport_addr
	ldrb r0, [r1, #0]
	mov	pc, lr

inport_addr:
	.word	INPORT_ADDRESS


; ---------------------------------------------
; global uninitialized data
    .section .bss


; ---------------------------------------------
; global initialized data
    .data

I:
	.word 0

I_last:
	.word 0


; ---------------------------------------------
; stack
    .section .stack
    .space STACK_SIZE
top_of_stack: