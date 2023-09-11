package pt.isel.ls.storage

import kotlinx.datetime.LocalDate
import pt.isel.ls.models.Board
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.Card
import pt.isel.ls.models.CardToBoardList
import pt.isel.ls.models.User
import java.util.UUID

/**
 * Storage contract for the tasks API. Defines the storage r/w access methods.
 */
interface IStorage {
    // / User management

    /**
     * Creates a new user with the given name, email, password and token.
     * Returns the created user, or null if the creation failed.
     */
    fun createUser(name: String, email: String, password: String, token: UUID): User?

    /**
     * Returns the user with the given ID, or null if the user does not exist.
     */
    fun getUser(userId: Int): User?

    /**
     * Returns user token by email and password.
     */
    fun getUserToken(email: String, password: String): String?

    /**
     * Returns user id by token.
     */
    fun getUserIdByToken(token: String): Int?

    // / Board management

    /**
     * Returns the list of boards owned by the user with the given ID.
     */
    fun getUserBoards(userId: Int, skip: Int, limit: Int): List<Board>

    /**
     * Adds the user with the given ID to the board with the given ID.
     */
    fun addUserToBoard(userId: Int, boardId: Int)

    /**
     * Creates a new board with the given name and description.
     * Returns the created board.
     */
    fun createBoard(name: String, description: String): Board

    /**
     * Returns the board with the given ID, or null if the board does not exist.
     */
    fun getBoard(boardId: Int): Board?

    /**
     * Search boards by given [data]
     */
    fun searchBoards(data: String): List<Board>

    // / List management

    /**
     * Creates a new list with the given name and adds it to the board with the given ID.
     * Returns the created list.
     */
    fun createBoardList(name: String, boardId: Int): BoardList

    /**
     * Returns the list of board lists associated with the board with the given ID.
     */
    fun getBoardLists(boardId: Int, skip: Int, limit: Int): List<BoardList>

    /**
     * Returns the list of users associated with the board with the given ID.
     */
    fun getBoardUsersList(boardId: Int, skip: Int, limit: Int): List<User>

    /**
     * Adds the given list to the board with the given ID.
     */
    fun addListToBoard(boardId: Int, list: BoardList)

    /**
     * Returns the list of cards associated with the list with the given ID.
     */
    fun getCardsFromList(listId: Int, skip: Int, limit: Int): List<Card>

    /**
     * Returns the list with the given ID, or null if the list does not exist.
     */
    fun getList(listId: Int): BoardList?

    /**
     * Deletes the list with the given ID.
     */
    fun deleteList(listId: Int)

    // / Card management

    /**
     * Creates a new card with the given information and adds it to the list with the given ID.
     * Returns the created card.
     */
    fun createCard(
        listId: Int,
        name: String,
        description: String,
        creationDate: LocalDate,
        conclusionDate: LocalDate?
    ): Card

    /**
     * Returns the card with the given ID, or null if the card does not exist.
     */
    fun getCard(cardId: Int): Card?

    /**
     * Moves the card with the given ID to the list with the given ID and its new index.
     */
    fun moveCard(cardId: Int, listId: Int, newIndex: Int)

    /**
     * Deletes the card with the given ID.
     */
    fun deleteCard(cardId: Int)

    /**
     * Returns the card to lists relation with its indexes by its list identifier.
     */
    fun getCardsToList(listId: Int): List<CardToBoardList>

    /**
     * Returns the list that owns the card by [cardId].
     */
    fun getListIdFromCard(cardId: Int): Int?

    /**
     * Inserts dummy data in memory for testing/debugging purposes.
     */
    fun insertDummyDataForTesting()
}
