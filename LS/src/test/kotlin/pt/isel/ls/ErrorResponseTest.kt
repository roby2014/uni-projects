package pt.isel.ls

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.api.token
import pt.isel.ls.errors.ErrorDTO
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ErrorResponseTest {
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
    fun `test error response object from get board that does not exist`() {
        val req = client(Request(Method.GET, "$uri/boards/191223").token(token))
        val obj = Json.decodeFromString<ErrorDTO>(req.bodyString())
        assertEquals(Status.NOT_FOUND, req.status)
        assertEquals(Status.NOT_FOUND.code, obj.errorCode)
        assertEquals("Board not found.", obj.errorDescription)
    }

    @Test
    fun `test error response object from get board invalid id`() {
        val req = client(Request(Method.GET, "$uri/boards/abc").token(token))
        val obj = Json.decodeFromString<ErrorDTO>(req.bodyString())
        assertEquals(Status.BAD_REQUEST, req.status)
        assertEquals(Status.BAD_REQUEST.code, obj.errorCode)
        assertEquals("Invalid 'id' parameter", obj.errorDescription)
    }

    @Test
    fun `test error response object from create board with missing field`() {
        val bodyStr = "{\"name\": \"My Board\"}"
        val req = client(Request(Method.POST, "$uri/boards").token(token).body(bodyStr))
        val obj = Json.decodeFromString<ErrorDTO>(req.bodyString())
        assertEquals(Status.BAD_REQUEST, req.status)
        assertEquals(Status.BAD_REQUEST.code, obj.errorCode)
        assertNotNull(obj.errorDescription)
    }
}
