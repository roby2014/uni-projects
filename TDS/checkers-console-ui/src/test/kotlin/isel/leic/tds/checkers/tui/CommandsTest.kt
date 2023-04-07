package isel.leic.tds.checkers.tui

import isel.leic.tds.checkers.model.Board
import isel.leic.tds.checkers.model.BoardStarting
import isel.leic.tds.checkers.model.Player
import isel.leic.tds.storage.FileStorage
import isel.leic.tds.storage.ISerializer
import java.io.File
import kotlin.test.*

class CommandsTest {

    // Dummy data for testing purposes

    private val serializer = object : ISerializer<Board, String> {
        override fun serialize(obj: Board) = obj.serialize()
        override fun deserialize(input: String) = Board.deserialize(input)
    }

    private val dummyName = "dummyGame"
    private val dummyTestFolder = "build"

    private val fs = FileStorage<String, Board>(dummyTestFolder, { BoardStarting() }, serializer)

    // Tests

    @BeforeTest
    fun setup() {
        File("$dummyTestFolder/$dummyName.txt").let { fp ->
            if (fp.exists()) fp.delete()
        }
    }

    @Test
    fun `exit should do nothing `() {
        val cmd = availableGameCommands(fs)["EXIT"]
        assertNotNull(cmd)
        assertNotNull(cmd.syntax)

        val action = cmd.action(null, emptyList())
        assertNull(action)
    }

    @Test
    fun `start with already running game`() {
        val cmd = availableGameCommands(fs)["START"]
        assertNotNull(cmd)
        assertFailsWith<IllegalArgumentException> {
            val dummyGame = Game(dummyName, BoardStarting(), Player.WHITE)
            cmd.action(dummyGame, listOf(dummyName))
        }.let { ex -> assertEquals( "Game already started", ex.message) }
    }

    @Test
    fun `start with no arguments`() {
        val cmd = availableGameCommands(fs)["START"]
        assertNotNull(cmd)
        assertFailsWith<IllegalArgumentException> {
            cmd.action(null, emptyList())
        }.let { ex -> assertEquals("Usage: ${cmd.syntax}", ex.message) }
    }

    @Test
    fun `play with no game running`() {
        val cmd = availableGameCommands(fs)["PLAY"]
        assertNotNull(cmd)
        assertFailsWith<IllegalArgumentException> {
            cmd.action(null, emptyList())
        }.let { ex -> assertEquals( "Game not started yet", ex.message) }
    }

    @Test
    fun `play with no arguments`() {
        val startCmd = availableGameCommands(fs)["START"]
        assertNotNull(startCmd)

        val game = startCmd.action(null, listOf(dummyName))
        assertNotNull(game)

        val cmd = availableGameCommands(fs)["PLAY"]
        assertNotNull(cmd)
        assertFailsWith<IllegalArgumentException> {
            cmd.action(game, emptyList())
        }.let { ex -> assertEquals("Usage: ${cmd.syntax}", ex.message) }
    }

    @Test
    fun `refresh with no game running`() {
        val cmd = availableGameCommands(fs)["REFRESH"]
        assertNotNull(cmd)
        assertNotNull(cmd.syntax)
        assertFailsWith<IllegalArgumentException> {
            cmd.action(null, emptyList())
        }.let { ex -> assertEquals( "Game not started yet", ex.message) }
    }

    @Test
    fun `refresh with unknown game`() {
        val cmd = availableGameCommands(fs)["REFRESH"]
        assertNotNull(cmd)
        assertNotNull(cmd.syntax)

        val randomName = "aaabbbcccdddeee"
        assertFailsWith<Exception> {
            val dummyGame = Game(randomName, BoardStarting(), Player.WHITE)
            cmd.action(dummyGame, emptyList())
        }.let { ex -> assertEquals( "Unknown game '${randomName}'...", ex.message) }
    }

    @Test
    fun `refresh should return not null after starting`() {
        // create new game
        val startCmd = availableGameCommands(fs)["START"]
        assertNotNull(startCmd)

        val game = startCmd.action(null, listOf(dummyName))
        assertNotNull(game)

        // refresh and check if we got an equal object not null
        val cmd = availableGameCommands(fs)["REFRESH"]
        assertNotNull(cmd)
        assertNotNull(cmd.syntax)

        val refreshedGame = cmd.action(game, emptyList())
        assertNotNull(refreshedGame)
        assertEquals(game, refreshedGame)
    }
}

