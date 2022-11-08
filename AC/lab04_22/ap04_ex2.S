; AC lab04 ex2
; Utilizar um Falling Edge Detector para interrupções do CPU
; Data:      18-05-2022

; Definicao dos valores dos simbolos utilizados no programa
;

	.equ	ENABLE_EXTINT, 0x10           ; EXTERNAL INTERRUPTION
	.equ	STACK_SIZE, 64                ; Dimensao do stack - 64 B
	.equ	OUTPORT_ADDRESS, 0xFF00       ; Endereco do porto de saida
	.equ	FED_ADDRESS, 0xFF80           ; Falling Edge Address
	.equ	VAR_INIT_VAL, 0               ; Valor inicial de var

; Seccao:    .startup
; Descricao: Guarda o código de arranque do sistema
;
	.section .startup
	b	_start
	ldr	pc, isr_addr
_start:
	ldr	sp, tos_addr
	ldr	pc, main_addr

tos_addr:
	.word	tos
main_addr:
	.word	main
isr_addr:
	.word	isr

; Seccao:    .text
; Descricao: Guarda o código do programa
;
	.text

; Rotina:    main
main:
	mov	r0, #VAR_INIT_VAL
	ldr	r1, var_addr_main
	strb	r0, [r1, #0]
	bl	outport_write
	ldr	r0, fed_addr_main
	strb	r0, [r0, #0]
	mrs	r0, cpsr
	mov	r1, #ENABLE_EXTINT
	orr	r0, r0, r1
	msr	cpsr, r0
main_loop:
	ldr	r0, var_addr_main
	ldrb	r0, [r0, #0]
	bl	outport_write
	b	main_loop

var_addr_main:
	.word	var
fed_addr_main:
	.word	FED_ADDRESS

; Rotina:    outport_write
; Descricao: Escreve num porto de saida a 8 bits o valor passado como argumento.
;            Interface exemplo: void outport_write( uint8_t value );
; Entradas:  r0 - valor a escrever no porto de saida
; Saidas:    -
; Efeitos:   r1 - guarda o endereco do porto alvo da escrita
outport_write:
	ldr	r1, outport_addr
	strb	r0, [r1, #0]
	mov	pc, lr

outport_addr:
	.word	OUTPORT_ADDRESS

; Rotina:    isr
isr:
	push	r0
	push	r1
	ldr	r0, fed_addr_isr
	strb	r2, [r0, #0]
	ldr	r0, var_addr_isr
	ldrb	r1, [r0, #0]
	add	r1, r1, #1
	strb	r1, [r0, #0]
	pop	r1
	pop	r0
	movs	pc, lr

var_addr_isr:
	.word	var
fed_addr_isr:
	.word	FED_ADDRESS

; Seccao:    .data
; Descricao: Guarda as variáveis globais com um valor inicial definido
;
	.data

; Seccao:    .bss
; Descricao: Guarda as variáveis globais sem valor inicial definido
;
	.section .bss
var:
	.space	1

; Seccao:    .stack
; Descricao: Implementa a pilha com o tamanho definido pelo simbolo STACK_SIZE
;
	.section .stack
	.space	STACK_SIZE
tos:
