package isel.leic.tds.checkers.model

/** Represents a board column, identified by [symbol]. */
class Column private constructor(val symbol: Char) {
    /** Represents the column index (a = 0, b = 1, etc...) */
    val index: Int = ('a'..'a' + BOARD_DIM).indexOf(symbol)

    companion object {
        /** List of possible columns */
        val values = List(BOARD_DIM) { Column('a' + it) }

        /**
         * When creating a Column object, this will be called instead,
         * and returns the column previously stored in the [values] list by its [symbol].
         */
        operator fun invoke(symbol: Char) = values.first { symbol == it.symbol }
    }
}

/**
 * Returns a [Column] with its symbol, or null if invalid symbol.
 * @receiver Symbol to convert to table index.
 */
fun Char.toColumnOrNull(): Column? = when (this) {
    !in ('a' until 'a' + BOARD_DIM) -> null
    else -> Column(this)
}

/**
 * Converts an integer index to a [Column] with its letter symbol.
 * @receiver Symbol index to convert.
 * @throws IndexOutOfBoundsException if index is bigger than board columns count
 */
fun Int.indexToColumn(): Column = when {
    this >= BOARD_DIM -> throw IndexOutOfBoundsException("Column index too big.")
    else -> Column('a' + this)
}