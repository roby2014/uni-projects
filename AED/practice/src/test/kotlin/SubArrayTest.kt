import kotlin.test.Test
import kotlin.test.assertEquals

class SubArrayTest {
    @Test
    fun testSubArrayQuadratic1() {
        val arr = intArrayOf(1, -2, 10, 3, -5, 2, 0)
        val (subArray, sum) = arr.maximalSubArrayQ(0, arr.size - 1)
        assertArrayEquals(intArrayOf(10,3), subArray)
        assertEquals(13, sum)
    }

    @Test
    fun testSubArrayQuadratic2() {
        val arr = intArrayOf(4, 12, 1, -2, -9, 9, -4, 3)
        val (subArray, sum) = arr.maximalSubArrayQ(0, arr.size - 1)
        assertArrayEquals(intArrayOf(4,12,1), subArray)
        assertEquals(17, sum)
    }

    @Test
    fun testSubArrayLinear1() {
        val arr = intArrayOf(1, -2, 10, 3, -5, 2, 0)
        val (subArray, sum) = arr.maximalSubArraySW(0, arr.size - 1)
        //assertArrayEquals(intArrayOf(10,3), subArray)
        //assertEquals(13, sum)
    }

    @Test
    fun testSubArrayLinear2() {
        val arr = intArrayOf(4, 12, 1, -2, -9, 9, -4, 3)
        val (subArray, sum) = arr.maximalSubArraySW(0, arr.size - 1)
        //assertArrayEquals(intArrayOf(4,12,1), subArray)
        //assertEquals(17, sum)
    }
}