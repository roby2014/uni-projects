package serie1

import serie1.countIncreasingSubArrays
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
class CountIncreasingSubArraysTest {

    /*
    countIncreasingSubArrays
que dado um array v de inteiros, retorna o número de sub-arrays
estritamente crescentes presentes em v com dimensão maior ou igual a 2.
Por exemplo, no caso de v = {1, 2, 4, 4, 5} este método deverá retornar 4,
 visto que existem os seguintes sub-arrays estritamente crescentes:
 {1, 2}, {1, 2, 4}, {2, 4} e {4, 5}.
     */
    @Test
    fun countIncreasingSubArrays_emptyArray() {
        val a = intArrayOf()
        assertEquals(0,countIncreasingSubArrays(a))
    }
    @Test
    fun countIncreasingSubArrays_singletonArray() {
        val a = intArrayOf(1)
        assertEquals(0,countIncreasingSubArrays(a))
    }
    @Test
    fun countIncreasingSubArrays_twoElements() {
        var a = intArrayOf(1,3)
        assertEquals(1,countIncreasingSubArrays(a))
        a = intArrayOf(1,0)
        assertEquals(0,countIncreasingSubArrays(a))
    }

    @Test
    fun countIncreasingSubArrays_fiveElements() {
        val a = intArrayOf(1,3,6,7,8)
        assertEquals(10,countIncreasingSubArrays(a))
    }

    @Test
    fun countIncreasingSubArrays_bitonicArray() {
        val a = intArrayOf(1,3,6,5,8)
        assertEquals(4,countIncreasingSubArrays(a))
    }

    @Test
    fun countIncreasingSubArrays_example() {
        val a = intArrayOf(1, 2, 4, 4, 5)
        assertEquals(4,countIncreasingSubArrays(a))
    }


}