package isel.leic.tds.checkers.model

import isel.leic.tds.checkers.model.Player.BLACK
import isel.leic.tds.checkers.model.Player.WHITE
import kotlin.math.abs

data class Piece(
    val player: Player,
    val pos: Square,
    val isQueen: Boolean = false
) {
    val symbol = if (isQueen) player.queenSymbol else player.symbol

    /**
     * Returns true if [this] piece can capture [other].
     * The list of [pieces] is also needed to  check edge cases.
     */
    fun canCapture(other: Piece, pieces: List<Piece>): Boolean {
        when {
            this == other -> return false
            player == other.player -> return false
            other.pos.row.index == 0 || other.pos.row.index == BOARD_DIM - 1 -> return false
            other.pos.column.index == 0 || other.pos.column.index == BOARD_DIM - 1 -> return false
        }

        val vDistance = other.pos.row.index - pos.row.index
        val hDistance = other.pos.column.index - pos.column.index

        if (abs(vDistance) != abs(hDistance)) return false

        return if (this.isQueen) {
            return !other.isGuarded(
                pieces.filter { it.player == other.player }, abs(vDistance),
                vDistance > 0, hDistance > 0
            )
        } else {
            if (abs(vDistance) != 1 || abs(hDistance) != 1) return false
            !other.isGuarded(
                pieces.filter { it.player == other.player }, abs(vDistance),
                vDistance > 0, hDistance > 0
            )
        }
    }

    /**
     * Verifies if the piece is guarded by another piece of the same player.
     * @param otherPieces Pieces that belong to the player
     * @param distance Distance from piece trying to capture to piece about to be captured
     * @param vDistancePositive Says if the vertical distance between the captor and piece is positive
     * @param vDistancePositive Says if the vertical distance between the captor and piece is positive
     */
    private fun isGuarded(
        otherPieces: List<Piece>, distance: Int,
        vDistancePositive: Boolean, hDistancePositive: Boolean
    ): Boolean {
        val vDistance = if (vDistancePositive) 1 else -1
        val hDistance = if (hDistancePositive) 1 else -1
        if (otherPieces.any {
                this.pos.row.index + vDistance == it.pos.row.index &&
                        this.pos.column.index + hDistance == it.pos.column.index
            }) return true
        if (otherPieces.any {
                this.pos.row.index - vDistance == it.pos.row.index &&
                        this.pos.column.index - hDistance == it.pos.column.index
            }) return true
        return diagonalBlocked(otherPieces, distance, vDistancePositive, hDistancePositive)
    }

    /**
     * Verifies if diagonal is blocked from the piece trying to capture to the piece about to be captured.
     * @param otherPieces Pieces that belong to the player
     * @param distance Distance from piece trying to capture to piece about to be captured
     * @param vDistancePositive Says if the vertical distance between the captor and piece is positive
     * @param vDistancePositive Says if the vertical distance between the captor and piece is positive
     */
    private fun diagonalBlocked(pieces :List<Piece>, distance: Int,
                                vDistancePositive: Boolean, hDistancePositive: Boolean) : Boolean {
        val vDistance = if (vDistancePositive) 1 else -1
        val hDistance = if (hDistancePositive) 1 else -1
        for (i in 2 until distance - 1) {
            if (!pieces.any {
                    this.pos.row.index - vDistance * i == it.pos.row.index &&
                            this.pos.column.index - hDistance * i == it.pos.column.index
                }) return false
            else if (pieces.any {
                    this.pos.row.index - vDistance * i == it.pos.row.index &&
                            this.pos.column.index - hDistance * i == it.pos.column.index
                }) return true
        }
        return false
    }

    /**
     * Checks when a piece reaches the opposite row.
     * Returns true when it does, or false otherwise
     */
    fun reachedOppositeRow(row: Row) =
        when (player) {
            WHITE -> row.number == BOARD_DIM
            BLACK -> row.number == 1
        }

    /**
     * Checks if queen is blocked from making a long-distance move
     * @param pieces Every piece on board
     * @param distance Distance travelled by queen
     * @param vDistancePositive Says if the vertical distance between the captor and piece is positive
     * @param vDistancePositive Says if the vertical distance between the captor and piece is positive
     */
    fun queenIsBlocked(
        pieces: List<Piece>, distance: Int, hDistancePositive: Boolean, vDistancePositive: Boolean
    ): Boolean {
        val queenPos = this.pos
        for (i in 1..distance) {
            val hDistance = if (hDistancePositive) i else -i
            val vDistance = if (vDistancePositive) i else -i
            if (
                pieces.any { piece ->
                    queenPos.row.index - vDistance == piece.pos.row.index &&
                            queenPos.column.index - hDistance == piece.pos.column.index
                }
            )
                return true
        }
        return false
    }

    fun serialize() = "${this.symbol}${pos.row.number}${pos.column.symbol}"

    companion object {
        fun deserialize(input: String): Piece {
            require(input.length == 3) { "Input cannot be converted to move" }
            val piece = input[0]
            val pos = input.drop(1).toSquareOrNull() ?: throw IllegalArgumentException("Invalid position")
            val pieceColor = if (piece == 'w' || piece == 'W') WHITE else BLACK
            val isQueen = piece == 'W' || piece == 'B'

            return Piece(pieceColor, pos, isQueen)
        }
    }
}
