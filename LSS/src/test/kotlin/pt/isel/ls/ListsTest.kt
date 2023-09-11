package pt.isel.ls

import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.api.routers.cards.CardDTO
import pt.isel.ls.api.token
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.Card
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ListsTest {
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
        val req = client(Request(GET, "$uri/ping"))
        assertEquals(req.status, Status.OK)
        assertEquals("pong", req.bodyString())
    }

    // Lists
    @Test
    fun `get list`() {
        val id = 1
        val req = client(Request(GET, "$uri/lists/$id").token(token))
        assertEquals(Status.OK, req.status)
        assertEquals(req.header("content-type"), "application/json")
        assertEquals(storage.getList(id), Json.decodeFromString<BoardList>(req.bodyString()))
    }

    @Test
    fun `get list that does not exist`() {
        val id = 19123
        val req = client(Request(GET, "$uri/lists/$id").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get list but invalid id`() {
        val req = client(Request(GET, "$uri/lists/abc").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `create card in list`() {
        val listId = 1

        val card = CardDTO("Card", "leet", LocalDate(2023, 3, 1))
        val req = client(Request(POST, "$uri/lists/$listId/cards").token(token).body(Json.encodeToString(card)))
        val createdCardId = req.bodyString().toInt()

        assertEquals(Status.CREATED, req.status)
        assertNotNull(createdCardId)
        assertTrue(createdCardId in storage.getCardsFromList(listId, 0, 100).map { it.id })
    }

    @Test
    fun `create card in list that does not exist`() {
        val listId = 19123
        val card = CardDTO("test", "test", null)
        val req = client(Request(POST, "$uri/lists/$listId/cards").token(token).body(Json.encodeToString(card)))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get cards in list`() {
        val listId = 1
        val req = client(Request(GET, "$uri/lists/$listId/cards").token(token))
        assertEquals(Status.OK, req.status)
        assertEquals(req.header("content-type"), "application/json")
        assertEquals(storage.getCardsFromList(listId, 0, 100), Json.decodeFromString<List<Card>>(req.bodyString()))
    }

    @Test
    fun `get cards in list that does not exist`() {
        val listId = 19123
        val req = client(Request(GET, "$uri/lists/$listId/cards").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get cards in list but invalid id`() {
        val req = client(Request(GET, "$uri/lists/abc/cards").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `delete list that does not exist`() {
        val id = 19123
        val req = client(Request(Method.DELETE, "$uri/lists/$id").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `delete list that invalid id in url`() {
        val req = client(Request(Method.DELETE, "$uri/lists/abc").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `delete list`() {
        val id = 3

        val deleteReq = client(Request(Method.DELETE, "$uri/lists/$id").token(token))
        assertEquals(Status.OK, deleteReq.status)

        val getReq = client(Request(GET, "$uri/lists/$id").token(token))
        assertEquals(Status.NOT_FOUND, getReq.status)
    }
}
