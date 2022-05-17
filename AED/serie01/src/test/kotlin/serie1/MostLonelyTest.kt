package serie1

import serie1.mostLonely
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class MostLonelyTest {
    @Test
    fun mostLonely_onArrayWithOneElement() {
        val a = intArrayOf(5)
        assertEquals(5, mostLonely(a))
    }

    @Test
    fun mostLonely_onArrayWithTwoElement() {
        val a = intArrayOf(5, 10)
        assertEquals(5, mostLonely(a))
    }

    @Test
    fun mostLonely_onArraySomeElements_Right() {
        val a = intArrayOf(10, 16, 1, 17, 25, 5)
        assertEquals(25, mostLonely(a))
    }


    @Test
    fun mostLonely_onArrayWithSomeElements_Middle() {
        val a = intArrayOf(10, 16, 1, 17, 5)
        assertEquals(10, mostLonely(a))
    }

    @Test
    fun mostLonely_onArraySomeElements_Left() {
        val a = intArrayOf(10, 16, 1, -10, 17, 25, 5)
        assertEquals(-10, mostLonely(a))
    }

    @Test
    fun mostLonely_withMoreThanOne() {
        val a = intArrayOf(15, 10, 17, 20, 5)
        assertEquals(5, mostLonely(a))
    }

    @Test
    fun mostLonely_withEquals() {
        val a = intArrayOf(5, 15, 10, 17, 10, 19, 5)
        assertEquals(15, mostLonely(a))
    }
}