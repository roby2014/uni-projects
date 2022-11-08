; Ficheiro:  lab03_sleep.S
; Descricao: lab03 de Arquitetura de Computadores.
; Autor:     Roberto & Pedro & Carolina
; Data:      04-05-2022

; Definicao dos valores dos simbolos utilizados no programa
;
	.equ	STACK_SIZE, 64                ; Dimensao do stack - 32 B
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
; Efeitos:   r0 - ritmo do contador (usado para sleep)
;			 r1 - guarda os endereços do porto
;            r2 - guarda o valor para escrever no led
main:
loop:
    mov r0, #1
	bl	outport_write
    
    bl inport_read
    bl get_sleep_time
    bl sleep
    
    mov r0, #0
    bl	outport_write

    bl inport_read
    bl get_sleep_time
    bl sleep

	b	loop

; Dorme X segundos, em que X é o valor do porto de entrada
; Retorna r0 (tempo a dormir)
; if r0 == 000 do r0 = 5
; else if r0 == 001 do r0 = 10 (1*5 + 5)
; else if r0 == 010 do r0 = 15 (2*5 + 5)
; else if r0 == 011 do r0 = 20 (3*5 + 5)
; else if r0 == 100 do r0 = 25
; else if r0 == 101 do r0 = 30
; else if r0 == 110 do r0 = 35
; else if r0 == 111 do r0 = 40
get_sleep_time:
    ; formula = (input_port_val * 5 + 5) segundos
    mov r3, 0x07 ; MASK
    and r0, r0, r3
    mov r3, #0
mult_by_5:
    add r0, r0, r0
    add r3, r3, #1
    mov r2, #4
    cmp r3, r2
    bne mult_by_5
mult_by_5_end:
	mov	pc, lr

; TODO:
get_led_status:

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

; Rotina:    sleep
; Descricao: Cria um "delay"/tempo inativo no programa de X tempo
; Entradas:  r0 - Tempo que queremos dar delay
; Saidas:    -
; Efeitos:   r0 - guarda o valor do iterador de tempo, quando chegar a 1, a função acaba
; 			 r1 - guarda o valor de um iterador alternativo?
sleep:
	and	r0, r0, r0
	beq	sleep_end
sleep_outer_loop:
	mov	r1, #0x3E
	movt r1, #0x03
sleep_inner_loop:
	sub	r1, r1, #1
	bne	sleep_inner_loop
	sub	r0, r0, #1
	bne	sleep_outer_loop
sleep_end:
	mov	pc, lr

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
