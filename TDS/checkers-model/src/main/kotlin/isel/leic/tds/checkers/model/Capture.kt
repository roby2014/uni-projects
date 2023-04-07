package isel.leic.tds.checkers.model

/**
 * Represents a possible capture by [captor].
 * @param pos The position of [captor] after capturing [captured].
 */
data class Capture(val captor: Piece, val captured: Piece, val pos: Square)

/**
 * Retrieves all capture possibilities,
 * including: the piece that captures,
 * the piece that is captured and the position to rest the piece on.
 */
fun getAllPlayerPossibleCaptures(currentPlayer: Player, pieces: List<Piece>): List<Capture> {
    val captureList = emptyList<Capture>().toMutableList()
    val allyPieces = pieces.filter { it.player == currentPlayer }
    val enemyPieces = pieces - allyPieces

    allyPieces.forEach { a ->
        enemyPieces.forEach { e ->
            if (a.canCapture(e, pieces)) {
                val toPos = getPossiblePositionsAfterCapture(a, e, pieces)
                toPos.forEach {
                    captureList.add(Capture(a, e, it))
                }
            }
        }
    }

    return captureList
}

/**
 * Returns the possible positions of [captor] after capturing the [captured] piece
 * Fact: if [captor] is not a queen, list size is always 1
 */
fun getPossiblePositionsAfterCapture(
    captor: Piece, captured: Piece, pieces: List<Piece>
): List<Square> {
    val vDistance = captured.pos.row.number - captor.pos.row.number
    val hDistance = captured.pos.column.symbol - captor.pos.column.symbol

    return if (captor.isQueen) {
        val possiblePos = mutableListOf<Square>()

        var vOffset = if (vDistance > 0) 1 else -1
        var hOffset = if (hDistance > 0) 1 else -1

        while (true) {
            val row = (captured.pos.row.number + vOffset).toRowOrNull() ?: break
            val column = (captured.pos.column.symbol + hOffset).toColumnOrNull() ?: break
            val toAdd = Square(row, column)

            if (pieces.find { it.pos == toAdd } != null)
                break

            possiblePos.add(toAdd)

            if (vOffset > 0) vOffset++ else vOffset--
            if (hOffset > 0) hOffset++ else hOffset--
        }

        possiblePos
    } else {
        listOf(
            Square(
                (captured.pos.row.number + vDistance).toRowOrNull()!!,
                (captured.pos.column.symbol + hDistance).toColumnOrNull()!!
            )
        )
    }
}


