package isel.leic.tds.checkers.gui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardStarting
import isel.leic.tds.storage.ISerializer
import isel.leic.tds.storage.MongoStorageAsync
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.system.exitProcess

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Checkers",
        state = rememberWindowState(width = WND_WIDTH.dp, height = WND_HEIGHT.dp)
    ) {
        // dependencies
        val scope = rememberCoroutineScope()
        val boardSerializer = object : ISerializer<Board, String> {
            override fun serialize(obj: Board) = obj.serialize()
            override fun deserialize(input: String) = Board.deserialize(input)
        }
        val database = KMongo.createClient(CONNECTION_STRING)
            .coroutine.getDatabase(DATABASE_NAME)
        val storage = MongoStorageAsync(COLLECTION_NAME, { BoardStarting() }, boardSerializer, database)

        // game object
        val game = remember { GameState(scope, storage) }

        // settings
        val targetOptions = remember { mutableStateOf(false) }
        val autoRefreshTargets = remember { mutableStateOf(false) }

        // top menu bar
        this.CheckersTopMenuBar(
            onGameStart = game::startGame,
            onGameLoad = game::loadGame,
            onGameRefresh = { game.refreshGame() },
            onGameExit = { exitProcess(0) },
            onOptionsTargets = {
                targetOptions.value = !targetOptions.value
            },
            onAutoRefreshTargets = {
                autoRefreshTargets.value = !autoRefreshTargets.value
                when (autoRefreshTargets.value) {
                    true -> game.enableAutoRefreshTargets()
                    false -> game.disableAutoRefreshTargets()
                }
            },
            onSetRefreshTime = { time ->
                game.changeRefreshTime(time)
            },
            targetOptions.value to autoRefreshTargets.value
        )

        // board view
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                BoardView(game.queuedFromPos, game.board?.board, targetOptions.value, game.board?.player) { pos ->
                    game.preparePlay(pos)
                }
            }
            DialogMessage(game.messageString, game::dismissMessage)
        }

    }
}

