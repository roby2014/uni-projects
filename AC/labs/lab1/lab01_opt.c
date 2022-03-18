// Multiply 2 numbers by using consecutive sums (optimized loop)

#include <stdio.h>
#include <stdint.h>

void main() {
    int M = 2, m = 10; // testing purposes
    int16_t p = 0;

    if (M < m) {
        int temp = M;
        M = m;
        m = temp;
    }

    if (M != 0) {
        while (m > 0) {
            printf("while\n");
            p += M;
            m--;
        }
    }
}