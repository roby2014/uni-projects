package isel.leic.tds.checkers.tui.commands

import isel.leic.tds.checkers.tui.Game

/** EXIT command implementation. */
object ExitCommand : ICommand<Game> {
    override val syntax get() = "EXIT"
    override fun action(game: Game?, args: List<String>) = null
    override fun show(game: Game) {}
}