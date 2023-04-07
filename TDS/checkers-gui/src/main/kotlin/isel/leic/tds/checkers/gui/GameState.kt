package isel.leic.tds.checkers.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardDraw
import isel.leic.tds.checkers.model.BoardWinner
import isel.leic.tds.checkers.model.Square
import isel.leic.tds.storage.MongoStorageAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Represents the state of the game, including the coroutine scope and the database storage.
 */
class GameState(private val scope: CoroutineScope, private val storage: MongoStorageAsync<Board>) {
    // All constants that represent either the state of the game or allow coroutines usage.
    private val gameState: MutableState<GameAsync?> = mutableStateOf(null)
    private val message: MutableState<String> = mutableStateOf("")
    private val queuedFromPosState: MutableState<Square?> = mutableStateOf(null)

    // settings
    var autoRefreshTargetsJob: Job? = null

    private val refreshTime = mutableStateOf(AUTO_REFRESH_TARGETS_TIME)

    // getters to extract the value of each state.
    val board get() = gameState.value
    val messageString get() = message.value
    val queuedFromPos get() = queuedFromPosState.value

    /**
     * Starts up a new game.
     * @param name Name ID of game.
     */
    fun startGame(name: String) {
        val autoWasActive = autoRefreshTargetsJob != null
        //If autoRefreshTargets was active, it should first disable it to avoid reloading the previous game
        disableAutoRefreshTargets()
        scope.launch {
            gameState.value = start(name, storage)
            queuedFromPosState.value = null
            val (game, setGame) = gameState
            if (game != null) {
                val newBoard = storage.load(game.name)
                if (newBoard != null && newBoard.pieces != game.board.pieces) {
                    setGame(GameAsync(game.name, newBoard, game.player))
                }
            }
        }
        //Turn the option back on if it was enabled beforehand.
        if (autoWasActive) enableAutoRefreshTargets()
    }

    /**
     * Loads game by [name].
     */
    fun loadGame(name: String) {
        val autoWasActive = autoRefreshTargetsJob != null
        // if autoRefreshTargets was active, it should first disable it to avoid reloading the previous game
        disableAutoRefreshTargets()
        scope.launch {
            gameState.value = load(name, storage)
            queuedFromPosState.value = null
            val (game, setGame) = gameState
            if (game != null) {
                val newBoard = storage.load(game.name)
                if (newBoard != null && newBoard.pieces != game.board.pieces) {
                    setGame(GameAsync(game.name, newBoard, game.player.nextPlayer()))
                }
            }
        }
        // turn the option back on if it was enabled beforehand.
        if (autoWasActive) enableAutoRefreshTargets()
    }

    /**
     * Refreshes game state.
     */
    fun refreshGame() {
        requireNotNull(gameState.value) { "Game not running... " }
        scope.launch {
            val (game, setGame) = gameState
            val newBoard = storage.load(game!!.name)
            if (newBoard != null && newBoard.pieces != game.board.pieces) {
                setGame(GameAsync(game.name, newBoard, game.player))
            }
        }
    }

    /**
     * Queues up a square to be used either as a from position or a pos position.
     * Depending on the state of queuedFromPos, it will be treated as from or to
     * @param pos Position queued up for either fromPos or toPos for play.
     */
    fun preparePlay(pos: Square) {
        if(pos.black) {
            if(playerPieceIsIn(pos))
                queuedFromPosState.value = pos
            else {
                if(queuedFromPos == null) return
                println("vai jogar de ${queuedFromPosState.value} para $pos")
                play(queuedFromPos!!, pos)
                queuedFromPosState.value = null
            }
        }
    }

    /**
     * Returns true if [pos] has a player's piece, false otherwise.
     */
    private fun playerPieceIsIn(pos: Square) =
        gameState.value?.board?.pieces?.any { it.pos == pos && it.player == board?.player } == true

    /**
     * Makes a new play, saving it on the db.
     */
    fun play(fromPos: Square, toPos: Square) {
        val (game, setGame) = gameState
        if (game == null) {
            message.value = "Please start a new game before playing."
            return
        }
        scope.launch {
            try {
                val newGame = game.play(fromPos, toPos, storage)
                setGame(newGame)
                message.value = setMessage(game.board)
                if (messageString != "") {
                    disableAutoRefreshTargets()
                }
            } catch (ex: Exception) {
                message.value = ex.message.toString()
            }
        }
    }

    /**
     * Sets up a message to then be presented on a dialogue box.
     */
    private fun setMessage(board: Board) = when (board) {
        is BoardWinner -> "Player ${board.winner} has already won!"
        is BoardDraw -> "The game ended in a draw."
        else -> ""
    }

    /**
     * Enables auto refresh targets options, and does its jobs,
     * which is refreshing the game automatically every 3 seconds,
     * on a new coroutine ([autoRefreshTargetsJob]).
     *
     * Throws [IllegalArgumentException] if game is not running.
     */
    fun enableAutoRefreshTargets() {
        requireNotNull(gameState.value) { "Game not running..." }
        autoRefreshTargetsJob = scope.launch {
            while (true) {
                val (game, setGame) = gameState
                val newBoard = storage.load(game!!.name)

                if (newBoard != null && newBoard.pieces != game.board.pieces) {
                    setGame(GameAsync(game.name, newBoard, game.player))
                }

                delay(refreshTime.value)
            }
        }
    }

    /**
     * Disables auto refresh targets option, stopping the [autoRefreshTargetsJob] coroutine job.
     */
    fun disableAutoRefreshTargets() {
        autoRefreshTargetsJob?.cancel()
        autoRefreshTargetsJob = null
    }

    /**
     * Changes [refreshTime] state value to [newRefreshTime].
     */
    fun changeRefreshTime(newRefreshTime: Int) {
        refreshTime.value = newRefreshTime.toLong()
    }

    /**
     * Clears [message] state value.
     */
    fun dismissMessage() {
        message.value = ""
    }
}