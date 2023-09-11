package pt.isel.ls

import kotlinx.serialization.json.Json
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.api.DEFAULT_LIMIT
import pt.isel.ls.api.DEFAULT_SKIP
import pt.isel.ls.api.token
import pt.isel.ls.models.Board
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.Card
import pt.isel.ls.models.User
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationTest {
    private val client = OkHttp()
    private val storage: IStorage = DataMemoryStorage // PostgresStorage
    private val server = tasksServer(storage, 9001)
    private val uri = "http://localhost:${server.port()}/api"
    private val token = storage.getUser(1)!!.token // hard coded

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
        val req = client(Request(Method.GET, "$uri/ping"))
        assertEquals(req.status, Status.OK)
        assertEquals("pong", req.bodyString())
    }

    // GET /boards/{id}/lists

    @Test
    fun `get board lists pagination`() {
        val boardId = 1
        val req = client(Request(Method.GET, "$uri/boards/$boardId/lists?limit=1&skip=0").token(token))
        val boardLists = Json.decodeFromString<List<BoardList>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(1, boardLists.size)
        assertEquals(storage.getBoardLists(boardId, 0, 1), boardLists)
    }

    @Test
    fun `get board lists pagination default parameters`() {
        val boardId = 1
        val req = client(Request(Method.GET, "$uri/boards/$boardId/lists?limit=abc&skip=abc").token(token))
        val boardLists = Json.decodeFromString<List<BoardList>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(storage.getBoardLists(boardId, DEFAULT_SKIP, DEFAULT_LIMIT), boardLists)
    }

    // GET /boards/{id}/users

    @Test
    fun `get board user lists pagination`() {
        val id = 1
        val req = client(Request(Method.GET, "$uri/boards/$id/users?limit=1&skip=0").token(token))
        assertEquals(Status.OK, req.status)

        val userList = Json.decodeFromString<List<User>>(req.bodyString())
        assertEquals(1, userList.size)
        assertEquals(storage.getBoardUsersList(id, 0, 1), userList)
    }

    @Test
    fun `get board user lists pagination default parameters`() {
        val id = 1
        val req = client(Request(Method.GET, "$uri/boards/$id/users?limit=abc&skip=abc").token(token))
        assertEquals(Status.OK, req.status)

        val userList = Json.decodeFromString<List<User>>(req.bodyString())
        assertEquals(storage.getBoardUsersList(id, DEFAULT_SKIP, DEFAULT_LIMIT), userList)
    }

    // GET /lists/{id}/cards

    @Test
    fun `get cards in list pagination`() {
        val listId = 1
        val req = client(Request(Method.GET, "$uri/lists/$listId/cards?limit=1&skip=0").token(token))
        val cards = Json.decodeFromString<List<Card>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(storage.getCardsFromList(listId, 0, 1), cards)
    }

    @Test
    fun `get cards in list pagination default parameters`() {
        val listId = 1
        val req = client(Request(Method.GET, "$uri/lists/$listId/cards?limit=abc&skip=abc").token(token))
        val cards = Json.decodeFromString<List<Card>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(storage.getCardsFromList(listId, DEFAULT_SKIP, DEFAULT_LIMIT), cards)
    }

    // GET /users/{id}/boards

    @Test
    fun `get user boards pagination`() {
        val id = 1
        val req = client(Request(Method.GET, "$uri/users/$id/boards?limit=1&skip=0").token(token))
        val userBoards = Json.decodeFromString<List<Board>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(1, userBoards.size)
        assertEquals(storage.getUserBoards(1, 0, 1), userBoards)
    }

    @Test
    fun `get user boards pagination default parameters`() {
        val id = 1
        val req = client(Request(Method.GET, "$uri/users/$id/boards?limit=abc&skip=abc").token(token))
        val userBoards = Json.decodeFromString<List<Board>>(req.bodyString())

        assertEquals(Status.OK, req.status)
        assertEquals(storage.getUserBoards(1, DEFAULT_SKIP, DEFAULT_LIMIT), userBoards)
    }
}
