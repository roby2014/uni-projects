package pt.isel.ls.services

import pt.isel.ls.api.DEFAULT_LIMIT
import pt.isel.ls.api.DEFAULT_SKIP
import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.NotFoundException
import pt.isel.ls.models.Board
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.User
import pt.isel.ls.storage.IStorage

/**
 * A service that provides functionality related to boards and board lists.
 *
 * @property storage The storage implementation to use for accessing and manipulating data.
 */
class BoardService(private val storage: IStorage) {

    /**
     * Creates a new board.
     *
     * @param name The name of the board to create.
     * @param description The description of the board to create.
     * @return The ID of the newly created board.
     * @throws Exception if there was a problem creating the board.
     *
     * HTTP Method: POST
     * URI: /boards
     */
    fun createBoard(name: String, description: String): Int {
        return storage.createBoard(name, description).id
    }

    /**
     * Adds a user to a board.
     *
     * @param userId The ID of the user to add to the board.
     * @param boardId The ID of the board to which the user will be added.
     * @throws NotFoundException if the board or user is not found.
     */
    fun addUserToBoard(userId: Int, boardId: Int) {
        storage.getBoard(boardId)
            ?: throw NotFoundException("Board not found.")

        storage.getUser(userId)
            ?: throw NotFoundException("User not found.")

        storage.addUserToBoard(userId, boardId)
    }

    /**
     * Retrieves detailed information about a board.
     *
     * @param boardId The ID of the board to retrieve.
     * @return A [Board] object representing the retrieved board.
     * @throws NotFoundException if the board is not found.
     */
    fun getBoard(boardId: Int): Board {
        return storage.getBoard(boardId)
            ?: throw NotFoundException("Board not found.")
    }

    /**
     * Creates a new list on a board.
     *
     * @param boardId The ID of the board on which to create the list.
     * @param listName The name of the list to create.
     * @return The ID of the newly created list.
     * @throws NotFoundException if the board is not found.
     */
    fun createBoardList(boardId: Int, listName: String): Int {
        storage.getBoard(boardId)
            ?: throw NotFoundException("Board not found.")

        val list = storage.createBoardList(listName, boardId)
        storage.addListToBoard(boardId, list)

        return list.id
    }

    /**
     * Retrieves the lists belonging to a board.
     *
     * @param boardId The ID of the board to retrieve lists from.
     * @param skip start position of the subsequence to return
     * @param limit length of the subsequence to return
     * @return A list of [BoardList] objects representing the retrieved lists.
     * @throws NotFoundException if the board is not found.
     */
    fun getBoardLists(boardId: Int, skip: Int, limit: Int): List<BoardList> {
        storage.getBoard(boardId)
            ?: throw NotFoundException("Board not found.")

        return storage.getBoardLists(boardId, skip, limit)
    }

    /**
     * Retrieves the lists of users that are related to a board.
     *
     * @param boardId The ID of the board to retrieve list from.
     * @param skip start position of the subsequence to return
     * @param limit length of the subsequence to return
     * @return A list of [BoardList] objects representing the retrieved lists.
     * @throws NotFoundException if the board is not found.
     */
    fun getBoardUsersList(boardId: Int, skip: Int, limit: Int): List<User> {
        storage.getBoard(boardId)
            ?: throw NotFoundException("Board not found.")

        return storage.getBoardUsersList(boardId, skip, limit)
    }

    /**
     * Tries to find boards by some given [data]
     */
    fun searchBoards(data: String): List<Board> {
        return storage.searchBoards(data)
    }

    /**
     * Returns true if the user that is associated to [token] belongs to the board by [boardId].
     */
    fun userInBoard(token: String, boardId: Int): Boolean {
        val boardUsers = getBoardUsersList(boardId, DEFAULT_SKIP, DEFAULT_LIMIT)
        if (boardUsers.isEmpty()) {
            return true // means this is the user trying to add himself
        }

        val userId = storage.getUserIdByToken(token) ?: throw BadRequestException("Invalid auth token")
        return userId in boardUsers.map { it.id }
    }
}
