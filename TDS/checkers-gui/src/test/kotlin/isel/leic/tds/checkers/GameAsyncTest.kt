package isel.leic.tds.checkers

import isel.leic.tds.checkers.gui.*
import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardStarting
import isel.leic.tds.checkers.model.Player
import isel.leic.tds.checkers.model.toSquareOrNull
import isel.leic.tds.storage.ISerializer
import isel.leic.tds.storage.MongoStorageAsync
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.test.*

class GameAsyncTests {
    private val dummyGameName = "test"
    private lateinit var storage: MongoStorageAsync<Board>

    @BeforeTest
    fun setupDb() {
        // Set up the tests dependencies
        val boardSerializer = object : ISerializer<Board, String> {
            override fun serialize(obj: Board) = obj.serialize()
            override fun deserialize(input: String) = Board.deserialize(input)
        }
        val database = KMongo.createClient(CONNECTION_STRING).coroutine.getDatabase(DATABASE_NAME)
        storage = MongoStorageAsync(COLLECTION_NAME, { BoardStarting() }, boardSerializer, database)

        // Remove "test" game, so we can manipulate it in each test as we want
        runBlocking {
            try {
                storage.delete(dummyGameName)
            } catch (ex: IllegalArgumentException) {
                // In case it does not exist, ok, skip
            }
        }
    }

    /** Runs before EACH test. */
    @Test
    fun `start game`() {
        runBlocking {
            // Start a new game
            val game = start(dummyGameName, storage)

            // Assert that the game object was created correctly
            assertEquals(dummyGameName, game.name)
            assertNotNull(game.board)
            assertEquals(Player.WHITE, game.player)
        }
    }

    @Test
    fun `start and load game`() {
        runBlocking {
            // Start a new game
            val game = start(dummyGameName, storage)

            // Load the game with the ID "test"
            val loadedGame = load(dummyGameName, storage)

            // Assert that the loaded game object has the same properties as the original game
            assertEquals(game.name, loadedGame.name)
            assertEquals(game.board, loadedGame.board)
            assertEquals(game.player, loadedGame.player)
        }
    }

    @Test
    fun `start, load and play a white piece`() {
        runBlocking {
            // Start a new game
            val game = start(dummyGameName, storage)

            // Play a WHITE piece from (3a) to (4b)
            val fromPos = "3a".toSquareOrNull()
            val toPos = "4b".toSquareOrNull()
            assertNotNull(fromPos)
            assertNotNull(toPos)

            val playedGame = game.play(fromPos, toPos, storage)

            // Assert that the game state was updated correctly
            assertEquals(game.name, playedGame.name)
            assertNotEquals(game.board, playedGame.board)
            assertEquals(Player.WHITE, playedGame.player)
        }
    }

    @Test
    fun `start, load and play with both players`() {
        runBlocking {
            // Start a new game (this will be the WHITE player)
            val gameW = start(dummyGameName, storage)

            // Play a WHITE piece from (3a) to (4b)
            val fromPosW = "3a".toSquareOrNull()
            val toPosW = "4b".toSquareOrNull()
            assertNotNull(fromPosW)
            assertNotNull(toPosW)

            val playedGameW = gameW.play(fromPosW, toPosW, storage)

            // Assert that the game state was updated correctly
            assertEquals(gameW.name, playedGameW.name)
            assertNotEquals(gameW.board, playedGameW.board)
            assertEquals(Player.WHITE, playedGameW.player)

            val fromPosB = "6b".toSquareOrNull()
            val toPosB = "5a".toSquareOrNull()
            assertNotNull(fromPosB)
            assertNotNull(toPosB)

            // Do the same, but for the BLACK player
            val gameB = load(dummyGameName, storage)
            val playedGameB = gameB.play(fromPosB, toPosB, storage)
            assertEquals(gameB.name, playedGameB.name)
            assertNotEquals(gameB.board, playedGameB.board)
            assertEquals(Player.BLACK, playedGameB.player)
        }
    }

    @Test
    fun `loading a game by an id that does not exist should throw`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                val game = load("asdifsaidfiqwjeqkjasldkf", storage)
            }
        }
        assertEquals("Game does not exist", ex.message)
    }

    @Test
    fun `same player can not play twice should throw`() {
        runBlocking {
            // Start a new game (this will be the WHITE player)
            val gameW = start(dummyGameName, storage)

            // Play a WHITE piece from (3a) to (4b)
            val fromPosW = "3a".toSquareOrNull()
            val toPosW = "4b".toSquareOrNull()
            assertNotNull(fromPosW)
            assertNotNull(toPosW)

            val playedGameW = gameW.play(fromPosW, toPosW, storage)

            // Assert that the game state was updated correctly
            assertEquals(gameW.name, playedGameW.name)
            assertNotEquals(gameW.board, playedGameW.board)
            assertEquals(Player.WHITE, playedGameW.player)

            val ex = assertFailsWith<IllegalArgumentException> {
                playedGameW.play(fromPosW, toPosW, storage)
            }
            assertEquals("You cannot play twice", ex.message)
        }
    }

    @Test
    fun `playing a piece that does not exist should throw`() {
        runBlocking {
            // Start a new game (this will be the WHITE player)
            val gameW = start(dummyGameName, storage)

            // Play a WHITE piece from (3a) to (4b)
            val fromPosW = "5a".toSquareOrNull()
            val toPosW = "1b".toSquareOrNull()
            assertNotNull(fromPosW)
            assertNotNull(toPosW)

            val ex = assertFailsWith<IllegalArgumentException> {
                gameW.play(fromPosW, toPosW, storage)
            }
            assertEquals("No such piece in that square exists", ex.message)
        }
    }

    @Test
    fun `playing a enemy piece should throw`() {
        runBlocking {
            // Start a new game (this will be the WHITE player)
            val gameW = start(dummyGameName, storage)

            // Play a WHITE piece from (3a) to (4b)
            val fromPosW = "6b".toSquareOrNull()
            val toPosW = "5a".toSquareOrNull()
            assertNotNull(fromPosW)
            assertNotNull(toPosW)

            val ex = assertFailsWith<IllegalArgumentException> {
                gameW.play(fromPosW, toPosW, storage)
            }
            assertEquals("You cannot move the other player's pieces", ex.message)
        }
    }

}
