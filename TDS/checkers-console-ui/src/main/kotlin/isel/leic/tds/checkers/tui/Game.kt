package isel.leic.tds.checkers.tui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.tui.commands.*
import isel.leic.tds.storage.IStorage

/**
 * Returns a map with the available game commands objects and its "calling" name.
 */
val availableGameCommands: (storage: IStorage<String, Board, String>) -> Map<String, ICommand<Game>> = { storage ->
    mapOf(
        "EXIT" to ExitCommand,
        "START" to StartCommand(storage),
        "PLAY" to PlayCommand(storage),
        "REFRESH" to RefreshCommand(storage),
    )
}

// Dummy data for now so we can compile
data class Game(val name: String, val board: Board, val player: Player) {
    /**
     * Prints game board.
     */
    fun print() {
        printDelimiters()
        // loop all rows and columns, check if the square has a piece, if it does, draw it
        // otherwise, draw "-" on the playable squares, and " " on the forbidden squares.
        for (row in BOARD_DIM downTo 1) {
            print("$row | ")
            for (col in 'a' until ('a' + BOARD_DIM)) {
                val square = "$row$col".toSquareOrNull()
                checkNotNull(square) { "Something went wrong at drawing the square on your board..." }

                when (val piece = board.pieces.find { it.pos == square }) {
                    null -> print(if (square.black) "- " else "  ")
                    else -> print("${piece.symbol} ")
                }
            }
            print("|")
            if (row != BOARD_DIM && row != BOARD_DIM -1) {
                println()
                continue
            }

            if (row == BOARD_DIM) {
                when (board) {
                    is BoardRun -> println(" Current player = ${board.currentPlayer.symbol}")
                    is BoardStarting -> println(" Current player = ${board.currentPlayer.symbol}")
                    else -> println()
                }
            } else if (row == BOARD_DIM-1) {
                when (board) {
                    is BoardRun -> println( "Turn = ${player.symbol}")
                    is BoardStarting -> println(" Turn = ${player.symbol}")
                    else -> println()
                }
            }
        }

        printDelimiters()
        print("    ")
        repeat(BOARD_DIM) { print("${'a' + it} ") } // prints columns symbols
        println()
        if (board is BoardDraw || board is BoardWinner) {
            println("Game ended.")
        }

    }

    private fun printDelimiters() {
        print("  ")
        print("+")
        repeat(BOARD_DIM * 2 + 1) { print("-") }
        println("+")
    }

}


