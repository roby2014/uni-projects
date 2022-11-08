; AC trabalho 1 2223i
; - Francisco Castelo (a49447@alunos.isel.pt)
; - Pedro Malafaia (a49506@alunos.isel.pt)
; - Roberto Petrisoru (a49418@alunos.isel.pt)

; 1. Sendo que cada instrução do processador P16 vale 16 bits (2 bytes), a nossa função tem 41 instruções,
; logo o total de bytes = 41*2 = 82 bytes.

; 2. A variável seed foi para a secção ".data" pois é uma variável global inicializada com o valor 1,
; como o seu tipo é de 32 bit, definimos 2 palavras (.word), a parte baixa tem o valor 1,
; e a alta fica a 0. Se por acaso a variável não fosse inicializada, era na secção ".bss".

; 3. RAND_MAX pode ser uma variável global ou uma constante. Se fosse uma variável global, iria
; estar na secção "data" pois irá ter um valor inicial, o que significa que estaria alocada
; na memória. Se fosse uma constante (.equ), o compilador iria procurar no ficheiro de código
; as suas ocorrências e substituir pelo seu valor. Neste caso achamos mais correto utilizar uma
; constante para não estár a desperdiçar memória, o valor é sempre o mesmo, portanto uma constante
; enquadra-se melhor.

; 4. Normalmente em rotinas do P16, o melhor é sempre declarar as variáveis em registos NÃO temporários,
; ou seja armazená-las no stack, e valores temporários serem nos registos r0/r1/r2/r3.
; Isto devido ao chamar outras funções, essas funções podem manipular os registos temporários, portanto a
; a resposta correta é R5. (mas pode ser em qualquer registo desde que esteja no stack)
;

; ---------------------------------------------
; constants
    .equ STACK_SIZE, 64
    .equ N, 10
    .equ RAND_MAX, 0xff

; ---------------------------------------------
; startup
    .section startup
    b _start
    b .

_start:
    ldr sp, sp_addr
    mov r0, pc
    add lr, r0, #4
    ldr pc, main_addr
    b .

sp_addr:
    .word top_of_stack

main_addr:
    .word main

; ---------------------------------------------
; code
    .text

; void main()
; r0/r1 = tmp
; r4 = i
; r5 = error
; r6 = rand_number
main:
    push lr
    push r4
    push r5
    push r6
    mov r4, #0 ; i = 0
    mov r5, #0 ; uint8_t error = 0
    ; 5423 = 0x152F
    mov r0, #0x2F
    movt r0, #0x15
    mov r1, #0
    mov r1, #0
    bl srand
for_loop:
    mov r0, #0
    cmp r5, r0 ; error == 0 ?
    bzc main_return
    mov r0, #N
    cmp r4, r0 ; i < N ?
    bhs main_return
    bl rand ; rand() returns in r0
    mov r6, r0 ; rand_number = rand()
main_if:
    ldr r0, result_addr
    ldr r0, [r0, r4] ; r0 = result[i]
    cmp r6,  r0 ; rand_number != result[i]
    beq for_inc
    mov r5, #1 ; error = 1
    b main_return
for_inc:
    add r4, r4, #2 ; i++
    b for_loop
main_return:
    mov r0, #0
    pop r4
    pop pc

result_addr:
    .word result


; uint32_t umull32(uint32_t M, uint32_t m)
; r1:r0 = m
; r3:r2 = M
; r6 = p_1
; r7 = i
; r8 = tmp
; return value stored in r0:r1
umull32:
    ; r5:r4:r1:r0 = p (aproveitar memoria porque nao vamos chamar outras rotinas)
    push r4
    push r5
    push r6 ; r6 = p_1
    push r7 ; r7 = i
    push r8 ; r8 = tmp
    ; int64_t p = m;
    mov r4, #0  ; p[16..23] = 0
    mov r5, #0  ; p[24..31] = 0
    mov r6, #0  ; uint8_t p_1 = 0;
umull32_for:
    mov r7, #0  ; i = 0
umull32_loop:
umull32_if:
    mov r8, #1
    and r8, r0, r8  ; (p & 0x1)
    bzc umull32_else_if
    mov r8, #1
    cmp r6, r8      ; p_1 == 1
    bne umull32_else_if
	add r4, r4, r2	;	p += M << 32
	adc r5, r5, r3
umull32_else_if:
    mov r8, #0x1
    and r8, r0, r8  ; (p & 0x1)
    bzs umull32_else
    mov r8, #0
    cmp r6, r8      ; p_1 == 0
    bne umull32_else
    sub r4, r4, r2 ;	p -= M << 32
    sbc r5, r5, r3
umull32_else:
    mov r8, #1
    and r8, r0, r8  ; r8 = p & 0x1
    mov r6, r8      ; p_1 = r8 = p & 0x1;
	asr r5, r5, #1  ; p = p >> 1
	rrx r4, r4		
	rrx r1, r1
	rrx r0, r0
    add r7, r7, #1 ; i++
    mov r8, #32
    cmp r7, r8
    blo umull32_loop ; if i < 32, loop again
umull32_for_end:
    ; return value is already stored in r1:r0
    pop r8
    pop r7
    pop r6
    pop r5
    pop r4
    mov pc, lr


; void srand (uint32_t nseed)
; nseed = r1:r0
; r2 = &seed
srand:
    ldr r2, seed_addr   ; r2 = &seed
    str r0, [r2, #0]    ; seed[0..15] = r0
    str r1, [r2, #2]    ; seed[16..31] = r1
    mov pc, lr


; uint16_t rand()
; r0/r1/r4/r5/r6 = tmp
; r2 = seed[0..15]
; r3 = seed[16..31]
; return value stored in r0
rand:
    push lr
    push r4
    ; prepare arguments for umull32(seed(r3:r2), 214013(r1:r0))
    ; seed = r3:r2
    ldr r0, seed_addr
    ldr r2, [r0, #0]
    ldr r3, [r0, #2]
    ; 214013 = 0x343FD = r1:r0
    mov r0, 0xfd
    movt r0, 0x43
    mov r1, 0x03
    movt r1, 0x00
    ; r1:r0 = umull32(seed, 214013)
    bl umull32
    ; add r1:r0 (32bits) with 2531011 = 0x269EC3 (24 bits)
    mov r4, 0xc3
    movt r4, 0x9e
    add r0, r0, r4
    mov r4, 0x26
    movt r4, 0x00
    adc r1, r1, r4
	bl get_mod		;modulo function which returns in (r1:r0):
    ldr r2, seed_addr
    ; seed = ... % RAND_MAX
    str r0, [r2, #0]
    str r1, [r2, #2]
    ; return seed >> 16
    mov r0, r1 ; seed >> 16
    mov r1, #0
    pop r4
    pop pc

seed_addr:
    .word seed


; uint32_t get_remainder(uint32_t num)
; num = r1:r0
; RAND_MAX = r2:r2
; tmp = r3
; return value stored in r1:r0
get_mod:
    mov r2, #RAND_MAX
    movt r2, #RAND_MAX
    ;  r1 r0     <- num/x
    ;  r2 r2     <- RAND_MAX/y
    ; if x = y  ->  x % y == 0
    ; if x < y  ->  x % y == x
    ; if x > y  ->  impossible bcuz RAND_MAX is the max uint 32 bit value 
get_mod_check:
    cmp r1, r2
    bne mod_not_zero
    cmp r0, r2
    bne mod_not_zero
    ; store return value
    mov r0, #0
    mov r1, #0
    b mod_return
mod_not_zero:
mod_return:
    mov pc, lr


; ---------------------------------------------
; global initialized data
    .data
result:
    .word 17747, 2055, 3664, 15611, 9819, 18005, 7515, 4525, 17337, 30985
seed:
    .word 0x0001
    .word 0x0000

; ---------------------------------------------
; stack
    .section .stack
    .space STACK_SIZE
top_of_stack:

b	.
