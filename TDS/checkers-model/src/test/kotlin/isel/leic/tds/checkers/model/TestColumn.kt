package isel.leic.tds.checkers.model

import kotlin.test.*

/**
 * The Column type identifies one of the columns on the board.
 * Columns are identified by a letter from 'a' to 'h' (if BOARD_DIM==8)
 * The left column is 'a'.
 * Assume BOARD_DIM in (1..('z'-'a'))
 */
class TestColumn {

    @Test
    fun `Symbol to Column with index property`() {
        val column = 'c'.toColumnOrNull()
        assertNotNull(column)
        assertEquals(2, column.index)
    }

    @Test
    fun `Index to Column with symbol property`() {
        val column = 3.indexToColumn()
        assertEquals('d', column.symbol)
    }

    @Test
    fun `Invalid index to Column throws IndexOutOfBounds`() {
        val ex = assertFailsWith<IndexOutOfBoundsException> {
            BOARD_DIM.indexToColumn()
        }
        assertEquals("Column index too big.", ex.message)
    }

    @Test
    fun `Invalid symbol to Column results null`() {
        val column = 'x'.toColumnOrNull()
        assertNull(column)
    }

    @Test
    fun `All valid symbols to columns`() {
        assertEquals(
            List(BOARD_DIM) { it },
            ('a'..'z').mapNotNull { it.toColumnOrNull()?.index }
        )
    }

    @Test
    fun `Get all valid values of Column`() {
        assertEquals(BOARD_DIM, Column.values.size)
        assertEquals(List(BOARD_DIM) { 'a' + it }, Column.values.map { it.symbol })
    }

    @Test
    fun `All invalid columns`() {
        val invalidChars = (0..255).map { it.toChar() } - ('a'..('a' + BOARD_DIM))
        val invalidColumns = invalidChars.mapNotNull { it.toColumnOrNull() }
        assertEquals(0, invalidColumns.size)
    }

    @Test
    fun `equals and identity of Columns`() {
        val column = Column('a')
        val col1 = 'a'.toColumnOrNull()
        assertNotNull(col1)
        val col2 = 0.indexToColumn()
        assertEquals(col1, col2)
        assertSame(col1, col2)
        assertSame(column, col1)
    }
}


