package serie1

import serie1.Point
import serie1.countEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
class CountEqualsTest {
    val array = arrayOf(Point(0,0))
    //val cmpPoints = {a: Point, b: Point -> a.x - b.x}
    val cmpPoints = {a: Point, b: Point ->
        if (a.x != b.x) a.x - b.x
        else a.y - b.y
    }

    @Test
    fun countEquals_onEmptyArray() {
        val a1 = arrayOf<Point>()
        val a2 = arrayOf(Point(0,0))
        assertEquals(0, countEquals(a1, a1, cmpPoints))
        assertEquals(0, countEquals(a1, a2, cmpPoints))
        assertEquals(0, countEquals(a2, a1, cmpPoints))
    }

    @Test
    fun countEquals_onOneElementArray() {
        val a1 = arrayOf(Point(0,0))
        val a2 = arrayOf(Point(1,1))
        val a3 = arrayOf(Point(0,1))
        assertEquals(1, countEquals(a1, a1, cmpPoints))
        assertEquals(0, countEquals(a1, a2, cmpPoints))
        assertEquals(0, countEquals(a1, a3, cmpPoints))
        assertEquals(0, countEquals(a2, a3, cmpPoints))
    }

    @Test
    fun countEquals_allPresent() {
        val a1 = arrayOf(Point(0,0), Point(0,1), Point(0, 2), Point(0, 5),
            Point(1, 3), Point(1, 1), Point(1, 7), Point(1, 9),
            Point(5, 5), Point(7, 3), Point(8, 9), Point(9, 9))
        val a2 = arrayOf(Point(0, 2), Point(0, 5),
            Point(1, 3), Point(1, 1), Point(1, 7),
            Point(5, 5), Point(8, 9), Point(9, 9))
        assertEquals(a1.size, countEquals(a1, a1, cmpPoints))
        assertEquals(a2.size, countEquals(a1, a2, cmpPoints))
    }

    @Test
    fun countEquals_nonePresent() {
        val a1 = arrayOf(Point(0,0), Point(0,1), Point(0, 2), Point(0, 5),
            Point(1, 3), Point(1, 1), Point(1, 7), Point(1, 9),
            Point(5, 5), Point(7, 3), Point(8, 9), Point(9, 9))
        val a2 = arrayOf(Point(2, 0), Point(2, 2),
            Point(3, 1), Point(3, 4), Point(3, 7),
            Point(5, 7), Point(7, 9), Point(8, 8))
        assertEquals(0, countEquals(a1, a2, cmpPoints))
        assertEquals(0, countEquals(a2, a1, cmpPoints))
    }

    @Test
    fun countEquals_somePresent() {
        val a1 = arrayOf(Point(0,0), Point(0,1), Point(0, 2), Point(0, 5),
            Point(1, 1), Point(1, 3), Point(1, 7), Point(1, 9),
            Point(5, 5), Point(7, 3), Point(8, 9), Point(9, 9))
        val a2 = arrayOf(Point(0, 7), Point(0, 9),
            Point(1, 1), Point(2, 2), Point(2, 3),
            Point(5, 5), Point(8, 8), Point(9, 9))
        assertEquals(3, countEquals(a1, a2, cmpPoints))
        assertEquals(3, countEquals(a2, a1, cmpPoints))
    }
}