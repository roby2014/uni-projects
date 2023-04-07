package isel.leic.tds.checkers.tui.commands

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.tui.Game
import isel.leic.tds.storage.IStorage


/**
 * REFRESH command implementation.
 * This is used to "sync" the board between the 2 players.
 * The board is synced via [storage].
 */
class RefreshCommand(private val storage: IStorage<String, Board, String>) : ICommand<Game> {

    override val syntax get() = "REFRESH"

    override fun action(game: Game?, args: List<String>): Game {
        requireNotNull(game) { "Game not started yet" }
        val newBoard = storage.load(game.name) ?: throw Exception("Unknown game '${game.name}'...")
        return game.copy(board = newBoard)
    }

    override fun show(game: Game) = game.print()
}