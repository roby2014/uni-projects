package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.model.Player.BLACK
import isel.leic.tds.checkers.model.Player.WHITE
import kotlin.math.abs

/**
 * Board class. Stores [pieces] made by the players.
 * Board status classes should inherit from this one,
 * status can be running, draw or won.
 */
sealed class Board(val pieces: List<Piece>) {

    /**
     * Turns the contents of the instance of Board and turns it into a string.
     * @return String of the contents of the board.
     */
    fun serialize(): String {
        val type = when (this) {
            is BoardStarting -> "${this::class.simpleName}:${turnsWithoutCapture}"
            is BoardRun -> "${this::class.simpleName}:${turnsWithoutCapture}"
            else -> "${this::class.simpleName}"
        }
        val commonInfo = type + "\n" + pieces.joinToString("\n") { it.serialize() }
        return when (this) {
            is BoardStarting -> commonInfo + "\n" + currentPlayer.serialize()
            is BoardRun -> commonInfo + "\n" + currentPlayer.serialize()
            is BoardDraw -> commonInfo
            is BoardWinner -> commonInfo + "\n" + winner.serialize()
        }
    }

    companion object {
        /**
         * Processes the string and converts it into a Board.
         * @param input String with contents of the board.
         * @return The board with the info in the string.
         * @throws IllegalStateException If no valid Board class is read.
         */
        fun deserialize(input: String): Board {
            val info = input.split("\n")
            val typeInfo = info[0].split(":")
            val type = typeInfo[0]
            val turnCount = if (typeInfo.size == 2) typeInfo[1].toInt() else null
            return if (type == BoardDraw::class.simpleName) {
                val pieces = info.drop(1).map { Piece.deserialize(it) }
                BoardDraw(pieces)
            } else {
                val player = Player.deserialize(info.last())
                val pieces = info.drop(1).dropLast(1).map { Piece.deserialize(it) }
                when (type) {
                    BoardStarting::class.simpleName -> BoardStarting(pieces, player, turnCount!!)
                    BoardRun::class.simpleName -> BoardRun(pieces, player, turnCount!!)
                    BoardWinner::class.simpleName -> BoardWinner(pieces, player)
                    else -> throw IllegalArgumentException("Invalid board given!")
                }
            }
        }

        /**
         * Creates "initial" board.
         * @return list of all initial pieces in their default position.
         */
        private fun createInitialBoard(): List<Piece> {
            val validSquares = Square.values.filter { it.black }
            val blackPieces = List(PIECES_PER_PLAYER) {
                Piece(BLACK, validSquares[it])
            }
            val whitePieces = List(PIECES_PER_PLAYER) {
                val idx = validSquares.size - 1 - it
                Piece(WHITE, validSquares[idx])
            }
            return blackPieces + whitePieces
        }

        val initialBoard = createInitialBoard()
    }

    abstract fun play(fromPos: Square, toPos: Square, player: Player): Board

}

/** If game ends in a draw, calling [Board.play] will call this. */
class BoardDraw(pieces: List<Piece>) : Board(pieces) {
    override fun play(fromPos: Square, toPos: Square, player: Player): Board {
        throw IllegalStateException("This game has already finished with a draw")
    }
}

/** If game ended with a win, calling [Board.play] will call this. */
class BoardWinner(pieces: List<Piece>, val winner: Player) : Board(pieces) {
    override fun play(fromPos: Square, toPos: Square, player: Player): Board {
        throw IllegalStateException("The player $winner already won this game")
    }
}

/**
 * Represents the board when starting. This is, when there is, at maximum, only one play that occurred from the start.
 */
class BoardStarting(
    pieces: List<Piece> = initialBoard,
    val currentPlayer: Player = WHITE,
    val turnsWithoutCapture: Int = 0
) : Board(pieces) {
    override fun play(fromPos: Square, toPos: Square, player: Player): Board {
        require(toPos.black) { "You cannot place a piece in that square!" }

        require(currentPlayer == player) { "You cannot play twice" }
        require(pieces.find { it.pos == toPos } == null) { "Square '$toPos' occupied" }

        val fromPiece = pieces.find { it.pos == fromPos }
        requireNotNull(fromPiece) { "No such piece in that square exists" }
        require(fromPiece.player == player) { "You cannot move the other player's pieces" }

        val hDistance = fromPos.column.index - toPos.column.index
        val vDistance = fromPos.row.index - toPos.row.index

        require(abs(vDistance) == abs(hDistance)) { "You can only move in diagonals" }

        if (!fromPiece.isQueen) {
            when (fromPiece.player) {
                WHITE -> require(toPos.row.number > fromPos.row.number) {
                    "You can't play backwards with a non Queen piece!"
                }

                BLACK -> require(toPos.row.number < fromPos.row.number) {
                    "You can't play backwards with a non Queen piece!"
                }
            }
            require(abs(vDistance) == 1) { "You can only move one row forward if there is no capture" }
        }

        val newList = pieces - fromPiece + Piece(player, toPos)
        return if (pieces == initialBoard)
            BoardStarting(newList, player.nextPlayer(), turnsWithoutCapture + 1)
        else BoardRun(newList, player.nextPlayer(), turnsWithoutCapture + 1)
    }

    override fun equals(other: Any?) =
        (other is BoardStarting) && pieces == other.pieces && currentPlayer == other.currentPlayer

    override fun hashCode() = pieces.hashCode() + currentPlayer.hashCode() * 31
}

/**
 * Represents the board while the game is running.
 */
class BoardRun(
    pieces: List<Piece>,
    val currentPlayer: Player = WHITE,
    val turnsWithoutCapture: Int = 0
) : Board(pieces) {
    /**
     * In case player can capture the enemy, it is forced to.
     * In case player gets to the opponent first line, the piece gets promoted.
     * In case player can capture, its turn only ends when he cannot capture anymore.
     */
    override fun play(fromPos: Square, toPos: Square, player: Player): Board {
        require(toPos.black) { "You cannot place a piece in that square!" }

        require(currentPlayer == player) { "You cannot play twice" }
        require(pieces.find { it.pos == toPos } == null) { "Square '$toPos' occupied" }

        val fromPiece = pieces.find { it.pos == fromPos }
        requireNotNull(fromPiece) { "No such piece in that square exists" }
        require(fromPiece.player == player) { "You cannot move the other player's pieces" }

        val hDistance = fromPos.column.index - toPos.column.index
        val vDistance = fromPos.row.index - toPos.row.index

        require(abs(vDistance) == abs(hDistance)) { "You can only move in diagonals" }

        val allPossibleCaptures = getAllPlayerPossibleCaptures(currentPlayer, pieces)
        if (allPossibleCaptures.isEmpty()) {
            if (!fromPiece.isQueen) {
                when (fromPiece.player) {
                        WHITE -> require(vDistance > 0) {
                        "You can't play backwards with a non Queen piece!"
                    }

                    BLACK -> require(vDistance < 0) {
                        "You can't play backwards with a non Queen piece!"
                    }
                }
                require(abs(vDistance) == 1) { "You can only move one row forward if there is no capture" }
            }
            else
                require(!fromPiece.queenIsBlocked(
                    pieces, abs(hDistance), hDistance > 0, vDistance > 0
             ))
            return update(fromPiece, toPos)
        }

        val captorsList = allPossibleCaptures.map { it.captor }
        require(fromPiece in captorsList) {
            val mandatoryCapture = allPossibleCaptures.map { it.captured }.first()
            "There is a mandatory capture in ${mandatoryCapture.pos}"
        }

        val fromPiecePossibleCaptures = allPossibleCaptures.filter { it.captor == fromPiece }
        val possiblePositionsAfterCapture = fromPiecePossibleCaptures.map { it.pos }
        require(toPos in possiblePositionsAfterCapture) {
            val mandatoryCapture = fromPiecePossibleCaptures.first().captured
            "There is a mandatory capture in " + "${mandatoryCapture.pos}"
        }

        val toRemove = fromPiecePossibleCaptures.first { it.pos == toPos }.captured
        return update(fromPiece, toPos, toRemove)
    }

    /**
     * Returns a new board after playing a [piece] to a new [toPos] position.
     * In case a captured occurred, [toRemove] piece is removed from the game
     */
    private fun update(piece: Piece, toPos: Square, toRemove: Piece? = null): Board {
        val enableQueen = piece.isQueen || piece.reachedOppositeRow(toPos.row)
        val newList = pieces - piece + Piece(piece.player, toPos, enableQueen)
        return if (toRemove != null) {
            val filteredList = newList - toRemove
            when {
                filteredList.all { it.player == WHITE } -> BoardWinner(filteredList, WHITE)
                filteredList.all { it.player == BLACK } -> BoardWinner(filteredList, BLACK)
                turnsWithoutCapture + 1 == STALEMATE_TURNS -> BoardDraw(filteredList)
                else -> {
                    if(getAllPlayerPossibleCaptures(currentPlayer, filteredList).isEmpty())
                        BoardRun(filteredList, currentPlayer.nextPlayer(), 0)
                    else
                        BoardRun(filteredList, currentPlayer, 0)
                }
            }
        } else
            if (turnsWithoutCapture + 1 == STALEMATE_TURNS)
                BoardDraw(newList)
            else
                BoardRun(newList, currentPlayer.nextPlayer(), turnsWithoutCapture + 1)
    }

    override fun equals(other: Any?) =
        (other is BoardRun) && pieces == other.pieces && currentPlayer == other.currentPlayer

    override fun hashCode() = pieces.hashCode() + currentPlayer.hashCode() * 31
}