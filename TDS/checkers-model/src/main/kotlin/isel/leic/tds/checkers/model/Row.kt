package isel.leic.tds.checkers.model

/** Represents a board row, where [number] is its board position. */
class Row private constructor(val number: Int) {
    /** Represents the row index. The index starts at the top by 0. */
    val index = BOARD_DIM - number

    companion object {
        /** List of possible rows */
        val values = List(BOARD_DIM) { Row(BOARD_DIM - it) }

        /**
         * When creating a Row object, this will be called instead,
         * and returns the column previously stored in the [values] list by its [symbol].
         */
        operator fun invoke(number: Int) = values.first { number == it.number }
    }
}

/**
 * Returns a [Row] object, or null if index is invalid.
 * @receiver Index to convert to table index.
 */
fun Int.toRowOrNull(): Row? = when (this) {
    in (1..BOARD_DIM) -> Row(this)
    else -> null
}

/**
 * Returns a [Row] object, or null if index is invalid.
 * @receiver Index to convert to table index.
 * @throws IndexOutOfBoundsException if index is bigger than table max rows.
 */
fun Int.indexToRow(): Row = when (this) {
    in (0 until BOARD_DIM) -> Row(BOARD_DIM - this)
    else -> throw IndexOutOfBoundsException("Row index too big.")
}
