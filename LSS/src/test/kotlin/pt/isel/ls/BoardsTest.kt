package pt.isel.ls

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.api.routers.boards.BoardDTO
import pt.isel.ls.api.token
import pt.isel.ls.models.Board
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.User
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BoardsTest {
    private val client = OkHttp()
    private val storage: IStorage = DataMemoryStorage // PostgresStorage
    private val server = tasksServer(storage, 9001)
    private val uri = "http://localhost:${server.port()}/api"
    private val token = storage.getUser(1)!!.token // WARNING: hard coded

    @BeforeTest
    fun setup() {
        if (storage is DataMemoryStorage) {
            storage.insertDummyDataForTesting()
        }

        server.start()
    }

    @AfterTest
    fun teardown() {
        server.stop()
    }

    @Test
    fun `responds to ping`() {
        val req = client(Request(GET, "$uri/ping"))
        assertEquals(req.status, Status.OK)
        assertEquals("pong", req.bodyString())
    }

    // get board

    @Test
    fun `get board`() {
        val id = 1
        val req = client(Request(GET, "$uri/boards/$id").token(token))
        assertEquals(Status.OK, req.status)
        assertEquals(req.header("content-type"), "application/json")
        assertEquals(storage.getBoard(id), Json.decodeFromString<Board>(req.bodyString()))
    }

    @Test
    fun `get board that does not exist`() {
        val id = 19123
        val req = client(Request(GET, "$uri/boards/$id").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get board but invalid id`() {
        val req = client(Request(GET, "$uri/boards/abc").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    // create board

    @Test
    fun `create board`() {
        val board = BoardDTO("Leet Project", "This is a leet project board")
        val req = client(Request(POST, "$uri/boards").token(token).body(Json.encodeToString(board)))
        assertEquals(Status.CREATED, req.status)
        assertEquals(req.header("content-type"), "application/json")
        assertNotNull(req.bodyString().toIntOrNull())
    }

    // add user to board

    @Test
    fun `add user to board`() {
        val boardId = 1
        val userId = 1
        val req = client(Request(POST, "$uri/boards/$boardId/users?userId=$userId").token(token))
        assertEquals(Status.OK, req.status)

        val userBoards = storage.getUserBoards(userId, 0, 100)
        assertTrue(userBoards.isNotEmpty())
        assertTrue(storage.getBoard(boardId) in userBoards)
    }

    @Test
    fun `add user to board with invalid board ID`() {
        val userId = 1
        val req = client(Request(POST, "$uri/boards/abc/users?userId=$userId").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `add user to board with missing user ID`() {
        val boardId = 1
        val req = client(Request(POST, "$uri/boards/$boardId/users").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `add user to board with non existent user ID`() {
        val boardId = 1
        val userId = 19123
        val req = client(Request(POST, "$uri/boards/$boardId/users?userId=$userId").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    // get board lists

    @Test
    fun `get board lists`() {
        val boardId = 1
        val req = client(Request(GET, "$uri/boards/$boardId/lists").token(token))
        val boardLists = Json.decodeFromString<List<BoardList>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(storage.getBoardLists(boardId, 0, 100), boardLists)
    }

    @Test
    fun `get board lists with invalid board ID`() {
        val req = client(Request(GET, "$uri/boards/abc/lists").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `get board lists with board ID that does not exist`() {
        val req = client(Request(GET, "$uri/boards/19041/lists").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    // create list

    @Test
    fun `create list`() {
        val req = client(Request(POST, "$uri/boards/1/lists?name=Test List").token(token))
        val createdListId = req.bodyString().toInt()
        assertEquals(Status.CREATED, req.status)
        assertNotNull(createdListId)
        assertNotNull(storage.getBoardLists(1, 0, 100).firstOrNull { it.id == createdListId })
    }

    @Test
    fun `create list with invalid board ID`() {
        val list = BoardList(1337, 1, "Test List", 1)
        val req = client(Request(POST, "$uri/boards/abc/lists").token(token).body(Json.encodeToString(list)))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `create list with board ID that does not exist`() {
        val list = BoardList(1337, 19230, "Test List", 1)
        val req = client(
            Request(POST, "$uri/boards/${list.boardId}/lists?name=\"Test List\"")
                .token(token).body(Json.encodeToString(list))
        )
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `create list with no list name parameter`() {
        val req = client(Request(POST, "$uri/boards/1/lists").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `get board user lists invalid board id`() {
        val req = client(Request(GET, "$uri/boards/abc/users").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `get board user lists board id that does not exist`() {
        val req = client(Request(GET, "$uri/boards/123456/users").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get board user lists`() {
        val req = client(Request(GET, "$uri/boards/1/users").token(token))
        assertEquals(Status.OK, req.status)

        val userList = Json.decodeFromString<List<User>>(req.bodyString())
        assertEquals(2, userList.size)
    }

    @Test
    fun `search board by id`() {
        val req = client(Request(GET, "$uri/search/boards?data=1").token(token))
        assertEquals(Status.OK, req.status)

        val boardList = Json.decodeFromString<List<Board>>(req.bodyString())
        val expectedBoard = storage.getBoard(1)
        val board = boardList[0]
        assertEquals(expectedBoard, board)
    }

    @Test
    fun `search board by exact name`() {
        val board = storage.getBoard(1)
        assertNotNull(board)

        val req = client(Request(GET, "$uri/search/boards?data=${board.name}").token(token))
        assertEquals(Status.OK, req.status)

        val boardList = Json.decodeFromString<List<Board>>(req.bodyString())
        assertTrue { boardList.contains(board) }
    }

    @Test
    fun `search board by exact description`() {
        val board = storage.getBoard(1)
        assertNotNull(board)

        val req = client(Request(GET, "$uri/search/boards?data=${board.description}").token(token))
        assertEquals(Status.OK, req.status)

        val boardList = Json.decodeFromString<List<Board>>(req.bodyString())
        assertTrue { boardList.contains(board) }
    }

    @Test
    fun `search board by random text (should not find any)`() {
        val req = client(Request(GET, "$uri/search/boards?data=c++geek").token(token))
        assertEquals(Status.OK, req.status)

        val boardList = Json.decodeFromString<List<Board>>(req.bodyString())
        assertEquals(0, boardList.size)
    }
}
