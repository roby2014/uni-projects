package serie1

import serie1.upperBound
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
class UpperBoundTest {

    var v1 = intArrayOf(0, 0, 1, 1, 4, 4)
    var v2 = intArrayOf(0, 0, 0, 1, 1, 1, 4, 4, 4)

    @Test
    fun upperBound_OnEmptyArray() {
        assertEquals(-1,upperBound(v1, 0, -1, 0))
        assertEquals(-1, upperBound(v1, 1, 0, 0))
        assertEquals(-1, upperBound(v1, v1.size, v1.size - 1, 4))
    }
    //último índice i tal que array[i]<=element.
    @Test
    fun upperBound_OnOneElementArray() {
        val a = IntArray(1)
        assertEquals(0, upperBound(a, 0, a.size - 1, 0))
        assertEquals(-1, upperBound(a, 0, a.size - 1, -1))
        assertEquals(0, upperBound(a, 0, a.size - 1, 1))
    }
    //último índice i tal que array[i]<=element.
    @Test
    fun upperBound_onBeginOfSequence() {
        assertEquals(1, upperBound(v1, 0, v1.size - 1, 0))
        assertEquals(2, upperBound(v2, 0, v2.size - 1, 0))
    }

    @Test
    fun countEqualTo_OnEndOfSequence() {
        assertEquals(5, upperBound(v1, 0, v1.size - 1, 4))
        assertEquals(8, upperBound(v2, 0, v2.size - 1, 4))
    }

    @Test
    fun countEqualTo_OnMiddle() {
        assertEquals(3, upperBound(v1, 0, v1.size - 1, 1))
        assertEquals(5, upperBound(v2, 0, v2.size - 1, 1))
    }

}