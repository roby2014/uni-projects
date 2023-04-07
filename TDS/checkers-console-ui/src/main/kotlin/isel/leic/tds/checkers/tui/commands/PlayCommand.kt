package isel.leic.tds.checkers.tui.commands

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.toSquareOrNull
import isel.leic.tds.checkers.tui.Game
import isel.leic.tds.storage.IStorage

/**
 * PLAY command implementation.
 * Plays the "from" piece to "to" position.
 * @return new board with the played position (or the same if some error occurred)
 */
class PlayCommand(private val storage: IStorage<String, Board, String>) : ICommand<Game> {

    override val syntax get() = "PLAY <from> <to>"

    override fun action(game: Game?, args: List<String>): Game {
        requireNotNull(game) { "Game not started yet" }
        require(args.size == 2) { "Usage: $syntax" }

        val from = args[0].toSquareOrNull() ?: throw Exception("Illegal square '${args[0]}'")
        val to = args[1].toSquareOrNull() ?: throw Exception("Illegal square '${args[1]}'")
        val newBoard: Board = game.board.play(from, to, game.player)

        storage.save(game.name, newBoard)
        return game.copy(board = newBoard)
    }

    override fun show(game: Game) = game.print()
}