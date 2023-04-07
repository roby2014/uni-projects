package isel.leic.tds.checkers.model

/**
 * Square represents a position on the board (Column and Row)
 * Squares are identified by one (or more) digits and one letter.
 */
class Square private constructor(val row: Row, val column: Column) {
    val black = (row.index + column.index) % 2 != 0

    override fun toString() = "${row.number}${column.symbol}"

    companion object {
        /** List of possible squares */
        val values = Row.values.map { row ->
            Column.values.map { col ->
                Square(row, col)
            }
        }.flatten()

        /**
         * When creating a Square object with 2 parameters (Row & Column), this will be called instead,
         * and returns the square previously stored in the [values] list, identified by [row] and [column].
         */
        operator fun invoke(row: Row, column: Column) = values.first { row == it.row && column == it.column }

        /**
         * When creating a Square object with 2 parameters (Row & Column), this will be called instead,
         * and returns the square previously stored in the [values] list, identified by [rowIdx] and [columnIdx].
         */
        operator fun invoke(rowIdx: Int, columnIdx: Int) =
            values.first { rowIdx == it.row.index && columnIdx == it.column.index }
    }
}

/**
 * Converts text to square, where the text is the row and column position
 * e.g: "8a" (if BOARD_DIM==8) would return Square(Row(8), Column('a'))
 */
fun String.toSquareOrNull(): Square? {
    if (this[0].isLetter()) return null

    val rowNr = this.filter { it.isDigit() }
    val row = rowNr.toIntOrNull()?.toRowOrNull() ?: return null

    val columnNr = this.last()
    val column = columnNr.toColumnOrNull() ?: return null

    return Square(row, column)
}