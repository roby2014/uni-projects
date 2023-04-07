package isel.leic.tds.checkers.model

import kotlin.test.*

/**
 * The Square type identifies a position on the board (Column and Row)
 * Squares are identified by one (or more) digits and one letter.
 * The top left is "8a" (if BOARD_DIM==8)
 */
class TestSquare {

    @Test
    fun `Dimensions limits`() {
        assert(BOARD_DIM in 2..26 step 2) { "BOARD_DIM must be a pair in 2..26" }
    }

    @Test
    fun `Create a black square and convert to string`() {
        assertTrue(BOARD_DIM > 2)
        val square = Square(1.indexToRow(), 2.indexToColumn())
        assertEquals("${BOARD_DIM - 1}${'a' + 2}", square.toString())
        assertTrue(square.black)
    }

    @Test
    fun `String to a white square and use index values`() {
        assertTrue(BOARD_DIM > 3)
        val square = "3d".toSquareOrNull()
        assertNotNull(square)
        assertEquals(3, square.column.index)
        assertEquals(BOARD_DIM - 3, square.row.index)
        assertFalse(square.black)
    }

    @Test
    fun `Invalid string to Square results null`() {
        assertNull("b3b".toSquareOrNull())
        assertNull("b3".toSquareOrNull())
        assertNull("3$".toSquareOrNull())
        assertNull("${BOARD_DIM + 1}a".toSquareOrNull())
        assertNull("1${'a' + BOARD_DIM}".toSquareOrNull())
    }

    @Test
    fun `All valid squares`() {
        val all = Square.values
        assertEquals(BOARD_DIM * BOARD_DIM, all.size)
        assertEquals("${BOARD_DIM}a", all.first().toString())
        assertEquals("1${'a' + BOARD_DIM - 1}", all.last().toString())
    }

    @Test
    fun `Identity of squares`() {
        val row = 3.toRowOrNull()
        assertNotNull(row)
        val col = 'c'.toColumnOrNull()
        assertNotNull(col)
        val square = "3c".toSquareOrNull()
        assertNotNull(square)
        val s1 = Square(row.index, col.index)
        val s2 = Square(row, col)
        assertSame(square, s1)
        assertSame(square, s2)
    }

    @Test
    fun `Square to string`() {
        val square = "3c".toSquareOrNull()
        assertNotNull(square)
        assertEquals("3c", square.toString())
    }
}

