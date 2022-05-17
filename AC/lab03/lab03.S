; Ficheiro:  lab03.S
; Descricao: Programa para a realização da 3a atividade laboratorial de
;            Arquitetura de Computadores.
; Autor:     Tiago M Dias (tiago.dias@isel.pt)
; Data:      04-05-2022

; Definicao dos valores dos simbolos utilizados no programa
;
	.equ	STACK_SIZE, 64                ; Dimensao do stack - 32 B
; *** Para completar ***
	.equ	INPORT_ADDRESS, 0xff00 
	.equ	OUTPORT_ADDRESS,0xff00 

; Seccao:    .startup
; Descricao: Guarda o código de arranque do sistema
;
	.section .startup
	b	_start
	b	.
_start:
	ldr	sp, tos_addr
	ldr	pc, main_addr

tos_addr:
	.word	tos
main_addr:
	.word	main

; Seccao:    .text
; Descricao: Guarda o código do programa
;
	.text

; Rotina:    main
; Descricao: Escreve o valor de r0 duas vezes no porto de saída (0xFF e 0), e de seguida entra 
;			num loop à espera de dados de entrada enviando-os também para o porto de saída
; Entradas:  -
; Saidas:    -
; Efeitos:   r0 - guarda valores adquiridos dos portos
;			 r1 - guarda os endereços do porto
main:
	mov	r0, #0xFF
	bl	outport_write
	mov	r0, #0x00
	bl	outport_write
loop:
	bl	inport_read
	bl	outport_write
	b	loop

; Rotina:    inport_read
; Descricao: Adquire e devolve o valor de um porto de entrada.
; Entradas:  -
; Saidas:    r0 - valor adquirido do porto de entrada
; Efeitos:   r1 - guarda o endereco do porto de entrada
inport_read:
	ldr	r1, inport_addr
	ldrb	r0, [r1, #0]
	mov	pc, lr

inport_addr:
	.word	INPORT_ADDRESS

; Rotina:    outport_write
; Descricao: Escreve num porto de saida o valor passado como argumento.
; Entradas:  r0 - valor a escrever no porto de saida
; Saidas:    -
; Efeitos:   r1 - guarda o endereco do porto de saida
outport_write:
	ldr	r1, outport_addr
	strb	r0, [r1, #0]
	mov	pc, lr

outport_addr:
	.word	OUTPORT_ADDRESS

; Seccao:    .data
; Descricao: Guarda as variáveis globais com um valor inicial definido
;
	.data

; Seccao:    .bss
; Descricao: Guarda as variáveis globais sem valor inicial definido
;
	.section .bss

; Seccao:    .stack
; Descricao: Implementa a pilha com o tamanho definido pelo simbolo STACK_SIZE
;
	.section .stack
	.space	STACK_SIZE
tos:
