package pt.isel.ls.storage

import io.github.cdimascio.dotenv.dotenv
import kotlinx.datetime.LocalDate
import pt.isel.ls.models.Board
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.BoardUser
import pt.isel.ls.models.Card
import pt.isel.ls.models.CardToBoardList
import pt.isel.ls.models.User
import java.util.UUID

object DataMemoryStorage : IStorage {
    private var users = mutableMapOf<Int, User>()
    private var boards = mutableMapOf<Int, Board>()
    private var boardUsers = setOf<BoardUser>()
    private var lists = mutableMapOf<Int, BoardList>()
    private var cards = mutableMapOf<Int, Card>()
    private var listsWithCards = setOf<CardToBoardList>()

    init {
        val api = System.getenv("API") ?: dotenv()["API"]
        // if dev mode, insert dummy data
        if (api == "DEV") {
            insertDummyDataForTesting()
        }
    }

    override fun createUser(name: String, email: String, password: String, token: UUID): User? {
        if (users.values.find { it.email == email } != null) {
            throw Exception("Email already exists.")
        }

        val user = User(getValidId(this::getUser), name, email, password, token.toString())
        users[user.id] = user
        return user
    }

    override fun getUser(userId: Int): User? {
        return users[userId]
    }

    override fun getUserToken(email: String, password: String): String? {
        return users.values.firstOrNull { it.email == email && it.password == password }?.token
    }

    override fun getUserIdByToken(token: String): Int? {
        return users.values.firstOrNull { it.token == token }?.id
    }

    override fun getUserBoards(userId: Int, skip: Int, limit: Int): List<Board> {
        val userBoardsIds = boardUsers.filter { it.userId == userId }
        val boardIds = userBoardsIds.map { it.boardId }
        return boards.values.filter { it.id in boardIds }.paginate(skip, limit)
    }

    override fun getBoard(boardId: Int): Board? {
        return boards[boardId]
    }

    override fun searchBoards(data: String): List<Board> {
        val result = mutableListOf<Board>()

        // maybe it is an id
        data.toIntOrNull()?.let { id ->
            getBoard(id)?.let { b ->
                result.add(b)
            }
        }

        // maybe it is a name or description
        val maybeByName = boards.values.filter { it.name.contains(data) }
        maybeByName.forEach { result.add(it) }

        val maybeByDesc = boards.values.filter { it.description.contains(data) }
        maybeByDesc.forEach { result.add(it) }

        return result
    }

    override fun createBoard(name: String, description: String): Board {
        val board = Board(getValidId(this::getBoard), name, description)
        boards[board.id] = board
        return board
    }

    override fun createBoardList(name: String, boardId: Int): BoardList {
        val list = BoardList(getValidId(this::getList), boardId, name, 0)
        lists[list.id] = list
        return list
    }

    override fun addListToBoard(boardId: Int, list: BoardList) {
        lists[list.id] = list.copy(boardId = boardId)
    }

    override fun addUserToBoard(userId: Int, boardId: Int) {
        boardUsers += BoardUser(boardId, userId)
    }

    override fun getBoardLists(boardId: Int, skip: Int, limit: Int): List<BoardList> {
        return lists.values.filter { it.boardId == boardId }.paginate(skip, limit)
    }

    override fun getBoardUsersList(boardId: Int, skip: Int, limit: Int): List<User> {
        return boardUsers.filter { it.boardId == boardId }.mapNotNull { getUser(it.userId) }.paginate(skip, limit)
    }

    override fun getList(listId: Int): BoardList? {
        return lists[listId]
    }

    override fun deleteList(listId: Int) {
        lists.remove(listId)
        listsWithCards = listsWithCards.filter { it.listId != listId }.toSet()
    }

    override fun getCardsFromList(listId: Int, skip: Int, limit: Int): List<Card> {
        val cardIds = listsWithCards.filter { c -> c.listId == listId }.map { c -> c.cardId }
        return cards.values.filter { c -> c.id in cardIds }.paginate(skip, limit)
    }

    override fun createCard(
        listId: Int,
        name: String,
        description: String,
        creationDate: LocalDate,
        conclusionDate: LocalDate?
    ): Card {
        val card = Card(getValidId(this::getCard), name, description, creationDate, conclusionDate)
        cards[card.id] = card
        val idx = listsWithCards.filter { it.listId == listId }.size
        listsWithCards += CardToBoardList(card.id, listId, idx)
        return card
    }

    override fun getCard(cardId: Int): Card? {
        return cards[cardId]
    }

    override fun moveCard(cardId: Int, listId: Int, newIndex: Int) {
        val orderedList = listsWithCards.filter { it.listId == listId && it.cardId != cardId }
            .sortedBy { it.index }
            .mapIndexed { idx, it -> it.copy(index = idx) }
        val newListTillIdx = orderedList.take(newIndex) + CardToBoardList(cardId, listId, newIndex)
        val secondWindow =
            orderedList.takeLast(orderedList.size - newIndex).mapIndexed { idx, it -> it.copy(index = newIndex + idx) }
        val newListCards = newListTillIdx + secondWindow
        listsWithCards = (listsWithCards.filterNot { it.listId == listId } + newListCards).toSet()
    }

    override fun deleteCard(cardId: Int) {
        cards.remove(cardId)
        listsWithCards = listsWithCards.filter { it.cardId != cardId }.toSet()
    }

    override fun getCardsToList(listId: Int): List<CardToBoardList> {
        return listsWithCards.toList().filter { it.listId == listId }
    }

    override fun getListIdFromCard(cardId: Int): Int? {
        return listsWithCards.first { it.cardId == cardId }.listId
    }

    /**
     * Generates a sequential and unique ID for object of type <T>
     * @param getMethod function that returns the <T> object by its ID.
     */
    private fun <T> getValidId(getMethod: (Int) -> T): Int {
        var id = 1
        while (getMethod(id) != null) {
            id++
        }
        return id
    }

    /**
     * Utility function that returns the paginated list using [skip] and [limit].
     * @param limit length of the subsequence to return
     * @param skip start position of the subsequence to return
     */
    private fun <T> List<T>.paginate(skip: Int, limit: Int): List<T> {
        // calculate the start and end indices for the sublist
        val startIndex = skip.coerceAtMost(this.size)
        val endIndex = (skip + limit).coerceAtMost(this.size)
        return this.slice(startIndex until endIndex)
    }

    /**
     * Inserts dummy data in memory for testing/debugging purposes.
     */
    override fun insertDummyDataForTesting() {
        users[1] = User(1, "roby", "roby@gmail.com", "12345", "token123")
        users[2] = User(2, "joao", "joao@hotmail.com", "12345", "token123")

        boards[1] = Board(1, "Project A", "This is a project board")
        boards[2] = Board(2, "Project B", "This is another project board")
        boards[3] = Board(3, "Project C", "This is another project C board")

        boardUsers = listOf(
            BoardUser(1, 1),
            BoardUser(1, 2),
            BoardUser(2, 1),
            BoardUser(3, 2)
        ).toSet()

        lists[1] = BoardList(1, 1, "Todo", 1)
        lists[2] = BoardList(2, 1, "In Progress", 2)
        lists[3] = BoardList(3, 2, "Todo", 1)

        cards[1] = Card(1, "Task 1", "Do homework", LocalDate(2023, 6, 2), null)
        cards[3] = Card(3, "Task 3", "Cook", LocalDate(2023, 1, 11), null)
        cards[5] = Card(5, "Task 5", "Go Skate", LocalDate(2023, 1, 30), null)

        listsWithCards = listOf(
            CardToBoardList(1, 1, 0),
            CardToBoardList(3, 1, 1),
            CardToBoardList(3, 2, 0),
            CardToBoardList(5, 3, 0)
        ).toSet()
    }
}
