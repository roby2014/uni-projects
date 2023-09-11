package pt.isel.ls

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.api.routers.users.LoginDTO
import pt.isel.ls.api.routers.users.UserDTO
import pt.isel.ls.api.token
import pt.isel.ls.errors.ErrorDTO
import pt.isel.ls.models.Board
import pt.isel.ls.models.User
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class UsersTest {
    private val client = OkHttp()
    private val storage: IStorage = DataMemoryStorage // PostgresStorage
    private val server = tasksServer(storage, 9001)
    private val uri = "http://localhost:${server.port()}/api"
    private val token = storage.getUser(1)!!.token // TODO: hard coded for now

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

    // create user tests

    @Test
    fun `creates user`() {
        val user = UserDTO("leet", "leet@1337.com", "12345")
        val userJson = Json.encodeToString(user)
        val req = client(Request(POST, "$uri/users").body(userJson))
        assertEquals(Status.CREATED, req.status)
        assertEquals(req.header("content-type"), "application/json")

        // val (token, id) = Json.decodeFromString<Pair<String, Int>>(req.bodyString())
        // assertNotNull(id)
        // assertNotNull(token)
        assert(req.bodyString().isNotEmpty())
    }

    @Test
    fun `creates user small password`() {
        val user = UserDTO("leet", "leet@1337.com", "1")
        val userJson = Json.encodeToString(user)
        val req = client(Request(POST, "$uri/users").body(userJson))
        assertNotEquals(Status.CREATED, req.status)
    }

    // get user tests

    @Test
    fun `get user`() {
        val id = 1
        val req = client(Request(GET, "$uri/users/$id").token(token))
        assertEquals(Status.OK, req.status)
        assertEquals(req.header("content-type"), "application/json")
        assertEquals(storage.getUser(id), Json.decodeFromString<User>(req.bodyString()))
    }

    @Test
    fun `get user that does not exist`() {
        val id = 19123
        val req = client(Request(GET, "$uri/users/$id").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get user but invalid id`() {
        val req = client(Request(GET, "$uri/users/abc").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    // get user board tests

    @Test
    fun `get user boards`() {
        val id = 1
        val req = client(Request(GET, "$uri/users/$id/boards").token(token))
        assertEquals(Status.OK, req.status)
        assertEquals(req.header("content-type"), "application/json")
        val userBoards = Json.decodeFromString<List<Board>>(req.bodyString())
        assertEquals(2, userBoards.size)
    }

    @Test
    fun `get user boards but user does not exist`() {
        val id = 19123
        val req = client(Request(GET, "$uri/users/$id/boards").token(token))
        assertEquals(Status.NOT_FOUND, req.status)
    }

    @Test
    fun `get user boards but invalid user id`() {
        val req = client(Request(GET, "$uri/users/abc/boards").token(token))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `login correct fields`() {
        val user = storage.getUser(1)
        assertNotNull(user)

        val loginData = LoginDTO(user.email, user.password)
        val loginJson = Json.encodeToString(loginData)
        val req = client(Request(POST, "$uri/login").body(loginJson))
        assertEquals(Status.OK, req.status)
        assertEquals("application/json", req.header("content-type"))

        assert(req.bodyString().isNotEmpty())
    }

    @Test
    fun `login incorrect fields`() {
        val loginData = LoginDTO("idk", "aaaa")
        val loginJson = Json.encodeToString(loginData)
        val req = client(Request(POST, "$uri/login").body(loginJson))
        assertNotEquals(Status.OK, req.status)

        val errorObj = Json.decodeFromString<ErrorDTO>(req.bodyString())
        assertEquals("Invalid credentials", errorObj.errorDescription)
    }

    @Test
    fun `auth missing token`() {
        val req = client(Request(GET, "$uri/auth"))
        assertEquals(Status.UNAUTHORIZED, req.status)
    }

    @Test
    fun `auth invalid token`() {
        val req = client(Request(GET, "$uri/auth").token("random"))
        assertEquals(Status.BAD_REQUEST, req.status)
    }

    @Test
    fun `auth valid token`() {
        val req = client(Request(GET, "$uri/auth").token(token))
        assertEquals(Status.OK, req.status)

        val res = Json.decodeFromString<Map<String, Int>>(req.bodyString())
        assertEquals(1, res["id"])
    }
}
