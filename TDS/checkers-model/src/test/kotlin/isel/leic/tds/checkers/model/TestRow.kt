package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.model.*
import kotlin.test.*

/**
 * The Row type identifies one of the rows on the board.
 * Rows are identified by a number, from 8 to 1 (if BOARD_DIM==8)
 * The bottom row is '0'
 * Assume BOARD_DIM > 0
 */
class TestRow {

    @Test
    fun `Number to Row with index property`() {
        val row = 3.toRowOrNull()
        assertNotNull(row)
        assertEquals(BOARD_DIM - 3, row.index)
    }

    @Test
    fun `Index to Row with number property`() {
        val row = 3.indexToRow()
        assertEquals(BOARD_DIM - 3, row.number)
    }

    @Test
    fun `Invalid index to Row throws IndexOutOfBounds`() {
        val ex = assertFailsWith<IndexOutOfBoundsException> {
            val row = BOARD_DIM.indexToRow()
        }
        assertEquals("Row index too big.", ex.message)
    }

    @Test
    fun `Invalid number to Row results null`() {
        val row = 0.toRowOrNull()
        assertNull(row)
    }

    @Test
    fun `All valid numbers to rows`() {
        assertEquals(
            (BOARD_DIM - 1 downTo 0).toList(),
            (1..BOARD_DIM).mapNotNull { it.toRowOrNull()?.index }
        )
    }

    @Test
    fun `Get all valid values of Rows`() {
        assertEquals(BOARD_DIM, Row.values.size)
        assertEquals((BOARD_DIM downTo 1).toList(), Row.values.map { it.number })
    }

    @Test
    fun `All invalid rows`() {
        val invalidNumbers = (-10..100) - (1..BOARD_DIM)
        val invalidRows = invalidNumbers.mapNotNull { it.toRowOrNull() }
        assertEquals(0, invalidRows.size)
    }
}
