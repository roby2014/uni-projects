package pt.isel.ls

import kotlinx.serialization.json.Json
import org.http4k.client.OkHttp
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.api.token
import pt.isel.ls.models.Card
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CardsTest {
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

    // Cards
    @Test
    fun `get card`() {
        val id = 1
        val req = client(Request(GET, "$uri/cards/$id").token(token))
        assertEquals(Status.OK, req.status)
        assertEquals(req.header("content-type"), "application/json")
        assertEquals(storage.getCard(id), Json.decodeFromString<Card>(req.bodyString()))
    }

    @Test
    fun `get card that does not exist`() {
        val id = 19123
        val req = client(Request(GET, "$uri/cards/$id").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get card but invalid id`() {
        val req = client(Request(GET, "$uri/cards/abc").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `move card to new index`() {
        val cardId = 1
        val listId = 1
        val newIndex = 1

        val listToCardsBefore = storage.getCardsToList(listId)
        println(listToCardsBefore)
        val req = client(Request(POST, "$uri/cards/$cardId?listId=$listId&newIndex=$newIndex").token(token))
        val listToCardsAfter = storage.getCardsToList(listId)
        println(listToCardsAfter)
        val newSupposedIndex = listToCardsAfter.filter { it.cardId == cardId && it.listId == listId }

        assertEquals(1, newSupposedIndex.size)
        assertNotEquals(listToCardsBefore, listToCardsAfter)
        assertEquals(newIndex, newSupposedIndex.first().index)
        assertEquals(Status.OK, req.status)
    }

    @Test
    fun `move card to new index 2`() {
        val cardId = 3
        val listId = 1
        val newIndex = 0

        val listToCardsBefore = storage.getCardsToList(listId)
        val req = client(Request(POST, "$uri/cards/$cardId?listId=$listId&newIndex=$newIndex").token(token))
        val listToCardsAfter = storage.getCardsToList(listId)
        val newSupposedIndex = listToCardsAfter.filter { it.cardId == cardId && it.listId == listId }

        assertEquals(1, newSupposedIndex.size)
        assertNotEquals(listToCardsBefore, listToCardsAfter)
        assertEquals(newIndex, newSupposedIndex.first().index)
        assertEquals(Status.OK, req.status)
    }

    @Test
    fun `move card from non-existent list`() {
        val cardId = 1
        val listId = 19123
        val req = client(Request(POST, "$uri/cards/$cardId?listId=$listId&newIndex=1").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `move card with invalid id`() {
        val listId = 2
        val req = client(Request(POST, "$uri/cards/abc?listId=$listId&newIndex=1").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `move card without listId`() {
        val cardId = 1
        val req = client(Request(POST, "$uri/cards/$cardId").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `delete card that does not exist`() {
        val id = 19123
        val req = client(Request(DELETE, "$uri/cards/$id").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `delete card that invalid id in url`() {
        val req = client(Request(DELETE, "$uri/cards/abc").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `delete card`() {
        val id = 5

        val deleteReq = client(Request(DELETE, "$uri/cards/$id").token(token))
        assertEquals(Status.OK, deleteReq.status)

        val getReq = client(Request(GET, "$uri/cards/$id").token(token))
        assertEquals(Status.NOT_FOUND, getReq.status)
    }
}
