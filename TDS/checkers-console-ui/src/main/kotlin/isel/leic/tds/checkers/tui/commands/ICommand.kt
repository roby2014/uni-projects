package isel.leic.tds.checkers.tui.commands

/** Interface for a UI command. */
interface ICommand<T> {
    /** Command's syntax usage. */
    val syntax: String

    /** Executed method when command is called. */
    fun action(obj: T?, args: List<String>): T?

    /** Executed method when action is successfully executed. */
    fun show(obj: T)
}