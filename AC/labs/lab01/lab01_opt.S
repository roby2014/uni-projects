; -----------------------------------------------------------------------------
; Ficheiro:  lab01_opt.S
; Descricao: Codigo da primeira atividade de laboratorio optimizado.
; Autores:	 - Tiago M Dias (tiago.dias@isel.pt)
;			 - Carolina
;			 - Pedro
;			 - Roberto
; Data:		 18-03-2022
; -----------------------------------------------------------------------------

	; r0 = M
	; r1 = m
	; r2 = p
	; r3 = guarda valores temporariamente

	mov	r2, #0

    cmp r0, r1
    bhs if
    mov r3, r0
    mov r0, r1
    mov r1, r3

if:
	mov r3, #0
	cmp r0, r3
	beq if_end
while:
	mov	r3, #0
	cmp	r3, r1
	bhs	while_end
	add	r2, r2, r0
	sub	r1, r1, #1
	b	while
while_end:
if_end:
	b	.
