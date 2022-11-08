#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#define N 10

uint16_t result[N] = {17747, 2055, 3664, 15611, 9819, 18005, 7515, 4525, 17337, 30985};

uint32_t seed = 1;

uint32_t umull32(uint32_t M, uint32_t m)
{

    int64_t p = m;
    uint8_t p_1 = 0;
    for (uint16_t i = 0; i < 32; i++)
    {
        if ((p & 0x1) == 0 && p_1 == 1)
        {
            p += (int64_t)M << 32;
        }
        else if ((p & 0x1) == 1 && p_1 == 0)
        {
            p -= (int64_t)M << 32;
        }
        p_1 = p & 0x1;
        p >>= 1;
    }
    return p;
}

void srand(uint32_t nseed)
{
    printf("nseed = %x\n", nseed);
    seed = nseed;
}

uint32_t get_remainder(uint32_t num, uint32_t divisor)
{
    /*printf("@get_remainder - %x mod %x\n", num, divisor);
    while (num >= divisor)
        num = num - divisor;
    return num;*/
    if (num == divisor)
        return 0;
    else
        return num;
}

uint16_t randdd(void)
{
    printf("result = %x\n", (umull32(seed, 214013) + 2531011));
    printf("get_remainder = %x\n", get_remainder((umull32(seed, 214013) + 2531011), 0xffFFffFF));
    seed = (umull32(seed, 214013) + 2531011) % (0xffFFffFF);
    printf("seed = %x\n\n", seed);

    return (seed >> 16);
}

int main(void)
{
    uint8_t error = 0;
    uint16_t rand_number;
    uint16_t i;
    srand(5423);
    for (i = 0; error == 0 && i < N; i++)
    {
        rand_number = randdd();
        if (rand_number != result[i])
        {
            error = 1;
        }
    }
    printf("error = %d\n", error);
    return 0;
}
