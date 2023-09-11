package pt.isel.ls

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import pt.isel.ls.api.DEFAULT_LIMIT
import pt.isel.ls.api.DEFAULT_SKIP
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.Card
import pt.isel.ls.models.CardToBoardList
import pt.isel.ls.models.User
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.IStorage
import pt.isel.ls.storage.PostgresStorage
import java.sql.Connection
import java.sql.Savepoint
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// WARNING: these tests depend on [insertDummyDataForTesting]
// if dummy data is changed, things should also be updated here!

class StorageTest {
    private var storage: IStorage = DataMemoryStorage // PostgresStorage
    private lateinit var sp: Savepoint
    private lateinit var conn: Connection

    init {
        if (storage is PostgresStorage) {
            conn = (storage as PostgresStorage).getConnection()
        }
    }

    @BeforeTest
    fun setup() {
        if (storage is PostgresStorage) {
            // if its database, we savepoint so we can roll back after test
            conn.autoCommit = false
            sp = conn.setSavepoint()

            // if its local testing, the insertDummyDataForTesting will just overwrite the data
        }

        storage.insertDummyDataForTesting()
    }

    @AfterTest
    fun teardown() {
        if (storage is PostgresStorage && ::sp.isInitialized) {
            conn.rollback(sp)
        }
    }

    @Test
    fun `create user`() {
        val user = storage.createUser("John Doe", "john@example.com", "password", UUID.randomUUID())
        assertNotNull(user)
        assertEquals("John Doe", user.name)
        assertEquals("john@example.com", user.email)
    }

    @Test
    fun `create user email already exists`() {
        val exception = assertFailsWith<Exception> {
            storage.createUser("rob", "roby@gmail.com", "password", UUID.randomUUID())
        }
    }

    @Test
    fun `get user`() {
        val expectedUser = User(1, "roby", "roby@gmail.com", "12345", "token123")
        val user = storage.getUser(1)
        assertEquals(expectedUser, user)
    }

    @Test
    fun `get user id does not exist`() {
        val user = storage.getUser(931451)
        assertNull(user)
    }

    @Test
    fun `get user token`() {
        val user = storage.getUser(1)
        assertNotNull(user)

        val token = storage.getUserToken(user.email, user.password)
        assertEquals(user.token, token)
    }

    @Test
    fun `get user token invalid credentials`() {
        val token = storage.getUserToken("asdfidk", "asdfasdf")
        assertNull(token)
    }

    @Test
    fun `get user by token`() {
        val expectedUser = storage.getUser(1)
        assertNotNull(expectedUser)

        val token = expectedUser.token
        val userId = storage.getUserIdByToken(token)
        assertEquals(expectedUser.id, userId)
    }

    @Test
    fun `get user by token invalid credentials`() {
        val userId = storage.getUserIdByToken("somerandomasdf2312312")
        assertNull(userId)
    }

    @Test
    fun `create board`() {
        val name = "Board 1"
        val description = "Board description"
        val board = storage.createBoard(name, description)

        assertEquals(name, board.name)
        assertEquals(description, board.description)
    }

    @Test
    fun `create board list`() {
        val name = "List 1"
        val boardId = 1
        val list = storage.createBoardList(name, boardId)

        assertEquals(name, list.name)
        assertEquals(boardId, list.boardId)
    }

    @Test
    fun `add list to board`() {
        val boardId = 1
        val list = BoardList(1, boardId, "List 1", 0)
        storage.addListToBoard(boardId, list)

        val retrievedList = storage.getList(list.id)
        assertNotNull(retrievedList)
        assertEquals(boardId, retrievedList.boardId)
    }

    @Test
    fun `add user to board`() {
        val userId = 2
        val boardId = 2
        storage.addUserToBoard(userId, boardId)

        val boardUsers = storage.getBoardUsersList(boardId, 0, Int.MAX_VALUE)
        val userIds = boardUsers.map { it.id }
        assertTrue(userId in userIds)
    }

    @Test
    fun `get board lists`() {
        val lists = storage.getBoardLists(1, 0, Int.MAX_VALUE)
        val expectedResult = listOf(
            BoardList(1, 1, "Todo", 1),
            BoardList(2, 1, "In Progress", 2)
        )

        expectedResult.forEach {
            assertTrue { it in lists }
        }
    }

    @Test
    fun `get board users list`() {
        val boardId = 1
        val expectedUsers = listOf(
            User(1, "roby", "roby@gmail.com", "12345", "token123"),
            User(2, "joao", "joao@hotmail.com", "12345", "token123")
        )

        val users = storage.getBoardUsersList(boardId, DEFAULT_SKIP, DEFAULT_LIMIT)
        assertEquals(expectedUsers.size, users.size)
        assertEquals(expectedUsers, users)
    }

    @Test
    fun `get list`() {
        val listId = 1
        val list = BoardList(1, 1, "Todo", 1)
        val retrievedList = storage.getList(listId)
        assertEquals(list, retrievedList)
    }

    @Test
    fun `delete list`() {
        val listId = 1
        storage.deleteList(listId)
        assertNull(storage.getList(listId))
    }

    @Test
    fun `get cards from list`() {
        val listId = 1
        val expectedCards = listOf(
            Card(1, "Task 1", "Do homework", LocalDate(2023, 6, 2), null),
            Card(3, "Task 3", "Cook", LocalDate(2023, 1, 11), null)
        )

        val cards = storage.getCardsFromList(listId, DEFAULT_SKIP, DEFAULT_LIMIT)
        assertEquals(expectedCards.size, cards.size)
        expectedCards.forEach { c ->
            assertTrue { c in cards }
        }
    }

    @Test
    fun `create card`() {
        val listId = 1
        val name = "Card 1"
        val description = "Card description"
        val creationDate = java.time.LocalDate.now().toKotlinLocalDate()
        val card = storage.createCard(listId, name, description, creationDate, null)

        assertEquals(name, card.name)
        assertEquals(description, card.description)
        assertEquals(creationDate, card.creationDate)
        assertNull(card.conclusionDate)
        // Add more assertions as needed
        // ...
    }

    @Test
    fun `get card`() {
        val cardId = 1
        val card = Card(1, "Task 1", "Do homework", LocalDate(2023, 6, 2), null)
        val retrievedCard = storage.getCard(cardId)
        assertEquals(card, retrievedCard)
    }

    @Test
    fun `move card`() {
        // TODO()
    }

    @Test
    fun `delete card`() {
        val cardId = 1
        storage.deleteCard(cardId)
        assertNull(storage.getCard(cardId))
    }

    @Test
    fun `get cards to list`() {
        val listId = 1
        val expectedCards = listOf(
            CardToBoardList(1, 1, 0),
            CardToBoardList(3, 1, 1)
        )

        val cards = storage.getCardsToList(listId)
        assertEquals(expectedCards.size, cards.size)
        assertEquals(expectedCards, cards)
    }
}
