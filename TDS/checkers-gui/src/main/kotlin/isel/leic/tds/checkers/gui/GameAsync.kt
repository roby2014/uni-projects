package isel.leic.tds.checkers.gui

import isel.leic.tds.checkers.model.*
import isel.leic.tds.checkers.model.Player.WHITE
import isel.leic.tds.storage.MongoStorageAsync

/**
 * Represents an async game object.
 * @param name Game name/ID
 * @param board Board object
 * @param player Current player
 */
class GameAsync(
    val name: String,
    val board: Board,
    val player: Player
)

/**
 * Starts a new game, with [name], overwriting if already exists.
 */
suspend fun start(name: String, storage: MongoStorageAsync<Board>): GameAsync {
    storage.load(name)?.let {
        storage.delete(name) // delete board, case game exists with given [name]
    }
    return GameAsync(name, storage.new(name), WHITE)
}

/**
 * Loads game by [name]. IT DOES NOT CREATE IF IT DOES NOT EXIST!
 */
suspend fun load(name: String, storage: MongoStorageAsync<Board>): GameAsync {
    val board: Board? = storage.load(name)
    requireNotNull(board) { "Game does not exist" }
    require ( board is BoardRun || board is BoardStarting )

    val currentPlayer = when(board){
        is BoardStarting -> board.currentPlayer
        is BoardRun -> board.currentPlayer
        else -> throw IllegalStateException("SHOULD NOT HAPPEN.")
    }
    return GameAsync(name, board, currentPlayer)
}

/**
 * Plays piece [fromPos] to [toPos], saving in [storage].
 */
suspend fun GameAsync.play(fromPos: Square, toPos: Square, storage: MongoStorageAsync<Board>): GameAsync {
    val board = this.board.play(fromPos, toPos, player)
    storage.save(name, board)
    return GameAsync(name, board, player)
}
