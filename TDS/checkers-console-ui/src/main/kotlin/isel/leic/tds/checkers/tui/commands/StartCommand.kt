package isel.leic.tds.checkers.tui.commands

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.Player
import isel.leic.tds.checkers.tui.Game
import isel.leic.tds.storage.IStorage
import isel.leic.tds.checkers.model.BoardStarting

/**
 * START command implementation.
 * Connects to a game, using [storage] for storing data.
 * If first connection, gets WHITE pieces, otherwise black
 * In case game already exists with >= 1 move, it creates a new one.
 */
class StartCommand(private val storage: IStorage<String, Board, String>) : ICommand<Game> {

    override val syntax get() = "START <gameName>"

    override fun action(game: Game?, args: List<String>): Game {
        require(game == null) { "Game already started" }
        require(args.size == 1) { "Usage: $syntax" }

        val gameName = args[0]
        val board = storage.load(gameName)

        return when(board) {
            null -> Game(gameName, storage.new(gameName), Player.WHITE)
            is BoardStarting -> Game(gameName, board, Player.BLACK)
            else -> {
                storage.delete(gameName)
                Game(gameName, storage.new(gameName), Player.WHITE)
            }
        }
    }

    override fun show(game: Game) = game.print()
}