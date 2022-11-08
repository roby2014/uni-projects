#include <stdio.h>
#include <stdio.h>
#include <stdint.h>

#define ARRAY_SIZE 12
int8_t array1[ARRAY_SIZE] = {24, 25, 29, 34, 38, 40, 41, 41, 39, 35, 30, 26};
int8_t array2[ARRAY_SIZE] = {-25, -22, -17, -5, 5, 11, 12, 9, 3, -7, -19, -24};

int8_t avg1, avg2;

int8_t average(int8_t a[], uint16_t n);
int16_t summation(int8_t a[], uint16_t n);
uint16_t udiv(uint16_t D, uint16_t d);

void main(void) {
    avg1 = average(array1, ARRAY_SIZE);
    avg2 = average(array2, ARRAY_SIZE);
    printf("avg1: %d\n\n", avg1);
    printf("avg2: %d", avg2);
    return;
}

int8_t average(int8_t a[], uint16_t n) {
    int8_t avg = INT8_MAX;
    uint16_t uacc, uavg;
    uint8_t neg;
    int16_t acc = summation(a, n);
    if (acc != INT16_MAX){
        if (acc < 0){
            neg = 1;
            uacc = -acc;
        }
        else{
            neg = 0;
            uacc = acc;
        }
        uavg = udiv(uacc, n);

        if (neg == 1){
            avg = -uavg;
        }
        else{
            avg = uavg;
        }
    }
    return avg;
}

int16_t summation(int8_t a[], uint16_t n){
    uint8_t error = 0;
    int16_t acc = 0;

    for (uint16_t i = 0; i < n && error == 0; i++){
        int16_t e = a[i];

        if ((e < INT16_MIN - acc) || (e > INT16_MAX - acc)){
            error = 1;
        }
        else{
            acc = acc + e;
        }
    }

    if (error == 1){
        acc = INT16_MAX;
    }

    return acc;
}

uint16_t udiv(uint16_t D, uint16_t d) {
    int32_t q = D;
    uint32_t shf_d = ((uint32_t)d) << 16;
    for (uint8_t i = 0; i < 16; i++) {
        q <<= 1;
        q -= shf_d;

        if (q < 0) {
            q += shf_d;
        }
        else {
            q |= 1;
        }
    }
    return q;
}