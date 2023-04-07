package isel.leic.tds.checkers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import isel.leic.tds.checkers.gui.COLLECTION_NAME
import isel.leic.tds.checkers.gui.CONNECTION_STRING
import isel.leic.tds.checkers.gui.DATABASE_NAME
import isel.leic.tds.checkers.gui.GameState
import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardStarting
import isel.leic.tds.storage.ISerializer
import isel.leic.tds.storage.MongoStorageAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.test.*

class GameStateTest {

    @Test
    fun `test start game`() {
        // Setup the test dependencies
        //val scope = ??? FIXME

        val boardSerializer = object : ISerializer<Board, String> {
            override fun serialize(obj: Board) = obj.serialize()
            override fun deserialize(input: String) = Board.deserialize(input)
        }
        val database = KMongo.createClient(CONNECTION_STRING)
            .coroutine.getDatabase(DATABASE_NAME)
        val mongo = MongoStorageAsync(COLLECTION_NAME, { BoardStarting() }, boardSerializer, database)

        // Create a new GameState object
        //val game = remember { GameState(scope, mongo) }

        // Start a new game with the ID "test123"
        //game.startGame("test123")

        // Assert that the game's board property is not null
        //assertNotNull(game.board)

        // Assert that the gameState's board property has the correct ID
        //assertEquals("test123", game.board!!.name)
    }

}