; -----------------------------------------------------------------------------
; Ficheiro:  lab01.S
; Descricao: Codigo de suporte a realizacao da primeira atividade laboratorial de AC.
; Autores:	 - Tiago M Dias (tiago.dias@isel.pt)
;			 - Carolina
;			 - Pedro
;			 - Roberto
; Data:		 17-03-2022
; -----------------------------------------------------------------------------

	; r0 = M
	; r1 = m
	; r2 = p
	; r3 = guarda valores temporariamente

	mov	r2, #0
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
