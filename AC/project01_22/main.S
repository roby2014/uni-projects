;; ----------------------------------------------------------------------------
	;; CONSTANTS
    .equ STACK_SIZE, 64
    .equ ARRAY_SIZE, 12
    .equ INT8_MAX, 0x7F
    .equ INT16_MIN, 0x8000
    .equ INT16_MAX, 0x7FFF
    

;; ----------------------------------------------------------------------------
    ;; startup section = PREPARE STACK AND CALL MAIN
    .section startup
    b _start
    b .
_start:
    ldr sp, sp_addr
    mov r0, pc
    add lr, r0, #4
    ldr pc, main_addr   ; pc <- main()
    b .
sp_addr:
    .word stack_addr
main_addr:
    .word main


;; ----------------------------------------------------------------------------
    ;; .text = CODE
    .text
main:
    push lr
        
    ;;; array 1
    ldr r0, array1_addr ; r0 = &array1[0]
    mov r1, ARRAY_SIZE  ; r1 = ARRAY_SIZE 
    bl average          ; average(r0,r1) - returns r0
    mov r2, avg1_addr   ; r2 = &avg1
    str r0, [r2]        ; *r2 = r0

    ;;; array 2
    ldr r0, array2_addr ; (...)
    mov r1, ARRAY_SIZE
    bl average          
    mov r2, avg1_addr   
    str r0, [r2]

    pop pc 
    b .

; addresses for indirect jumps
array1_addr:
    .word array1
array2_addr:
    .word array2
avg1_addr:
    .word avg1
avg2_addr: 
    .word avg2

;
; int8_t average(int8_t a[], uint16_t n)
; params:  r0/r8 = &a[0], r1 = n
; returns:  r0 (avg = r2)
; vars:  r2 = avg, r3/r8 = temp, r4 = uacc, 
;        r5 = uavg, r6 = neg, r7 = acc
average:
    push lr
    push r4             
    push r5
    push r6
    push r7
    push r8
    mov r8, r0                      ; temp store &a[0] in r8
    bl summation                    ; summation returns r0
    mov r7, r0                      ; acc = summation(a, n);
    mov r0, r8                      ; get back &a[0]
    mov r2, #INT8_MAX	            ; int8_t avg = INT8_MAX
    mov r3, #INT16_MAX & 0xFF
    movt r3, #(INT16_MAX >> 8)
    cmp r7, r3                      ; if (acc != INT16_MAX)
    beq average_ret             
    mov r3, #0                  
    cmp r7, r3                      ; if (acc < 0) {
    bge average_first_else      
    mov r6, #1                      ;  neg = 1
    mvn r3, r7
    add r3, r3, #1
    mov r4, r3                      ;  uacc = -acc
    b average_first_else_end        ; }
average_first_else:                 ; else {
    mov r6, #0                      ;  neg = 0
    mov r4, r7                      ;  uacc = acc
average_first_else_end:             ; }
    mov r8, r0                      ; temp store &a[0] in r8
    push r2                         ; temp store avg in stack for later (udiv can change r2)
    mov r0, r4                      ; r0 = uacc (udiv 1st param)
    bl udiv_fn                      ; udiv returns r0
    mov r5, r0                      ; uavg = udiv(uacc, n)
    pop r2                          ; get avg back to r2
    mov r0, r8                      ; get &a[0] back to r0
    mov r3, #1
    cmp r6, r3                      ; if (neg == 1) {
    bne average_second_else
    mvn r3, r5                  
    add r3, r3, #1              
    mov r2, r3                      ;   avg = -uavg
    b average_second_else_end       ; else { 
average_second_else:             
    mov r2, r5                      ;   avg = uavg
average_second_else_end:            ; }
average_ret:
    mov r0, r2                      ; return avg
    pop r8    
    pop r7             
    pop r6
    pop r5
    pop r4
    pop pc

;
; int16_t summation(int8_t a[], uint16_t n)
; params:  r0 = &a[ARRAY_SIZE], r1 = n
; returns:  r0 (acc = r3)
; vars:  r2 = error, r3 = acc, r4 = i, r5 = e, r6/r7 = temp
summation:
    push lr
    push r4
    push r5
    push r6
    push r7
    mov r2, #0                        ; uint8_t error = 0
    mov r3, #0                        ; int16_t acc = 0 
    mov r4, #0                        ; uint16_t i = 0
sum_for:
	cmp r4, r1		                  ; i < n
	bge sum_for_exit
	mov r6, #0
	cmp r2, r6		                  ; error == 0
	bne sum_for_exit
	ldrb r5, [r0, r4]	              ; e = a[i]   
	lsl r5, r5, #8	                  ; sign extension
	asr r5, r5, #8 	                  ; 
sum_for_if_cond1:
	mov r6, #0                        
	cmp r3, r6                        ; acc < 0
	bge sum_for_if_cond2
	mov r6, #INT16_MIN & 0x00        
	movt r6, #(INT16_MIN >> 8)
	sub r6, r6, r3                    ; INT16_MIN - acc
	cmp r5, r6                        ; e < INT16_MIN - acc
	blt sum_for_if
sum_for_if_cond2:
	mov r6, #0
	cmp r6, r3                        ; acc > 0
	bge sum_for_else
	mov r7, #INT16_MAX & 0xFF          
	movt r7, #(INT16_MAX >> 8)
	sub r6, r7, r3                    ; INT16_MAX - acc 
	cmp r6, r5                        ; INT16_MAX - acc < e
	bge sum_for_else      
sum_for_if:
	mov r2, #1		                  ; error = 1
	b sum_for
sum_for_else:
	add r3, r3, r5	                  ; acc = acc + e
	add r4, r4, #1	                  ; i++
	b sum_for
sum_for_exit:
	mov r6, #1
	cmp r2, r6
	bne sum_end
	mov r3, #INT16_MAX & 0xFF         
	movt r3, #(INT16_MAX >> 8)
sum_end:
	mov r0, r3      ; return acc
	pop r7
	pop r6
	pop r5
	pop r4
	pop pc

;
; uint16_t udiv (uint16_t D , uint16_t d)
; params: r0 = D, r1 = d
; returns: r0 (q = r3:r2)
; vars: r5:r4 = shf_d, r6 = i, r7 = temp
udiv_fn:
    push lr
    push r4
    push r5
    push r6
    push r7
    mov r2, r0              ; q = D
    mov r3, #0x00           ; q = D
    mov r4, #0x00           ; shf_d = d << 16         
    mov r5, r1              ; shf_d = d << 16
udiv_for:
    mov r6, #0              ; i = 0
udiv_for_cond: 
    mov r7, #16                   
    cmp r6, r7              ; i < 16
    bge udiv_for_end        
    add r6, r6, #1          ; i++ (podemos fazer aqui)
    lsl r2, r2, #1          ; q = q << 1 
    adc r3, r3, r3          ; add carry
    sub r2, r2, r4          ; q -= shf_d
    sbc r3, r3, r5          ; q -= shf_d
udiv_if_cond:
    mov r7, #0
    cmp r3, r7              ; if (q < 0)
    bge udiv_else_cond      ; {
    add r2, r2, r4          ;   q += shf_d;
    adc r3, r3, r5          ;   q += shf_d;
    b udiv_for_cond         ; }
udiv_else_cond:             ; else {
    mov r7, #1
    orr r2, r2, r7          ;   q |= 1
    b udiv_for_cond         ; }
udiv_for_end:
udiv_return:
    mov r0, r2              ; return q
    pop r7
    pop r6
    pop r5
    pop r4
    pop pc


;; ----------------------------------------------------------------------------
    ;; .data = INITIALIZED GLOBAL DATA
    .data
array1:
    .byte  24 , 25 , 29 , 34 , 38 , 40 , 41 , 41 , 39 , 35 , 30 , 26
array2:
    .byte -25 , -22 , -17 , -5, 5 , 11 , 12 , 9, 3, -7, -19 , -24


;; ----------------------------------------------------------------------------
    ;; .bss = NON INITIALIZED GLOBAL DATA
    .section .bss
avg1:
	.space INT8_MAX 
avg2:
	.space INT8_MAX


;; ----------------------------------------------------------------------------
    ;; .stack = STACK STARTS HERE
    .section .stack
    .space STACK_SIZE
stack_addr:
