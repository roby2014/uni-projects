package pt.isel.ls.services

import kotlinx.datetime.toKotlinLocalDate
import pt.isel.ls.api.DEFAULT_LIMIT
import pt.isel.ls.api.DEFAULT_SKIP
import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.NotFoundException
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.Card
import pt.isel.ls.storage.IStorage
import java.time.LocalDate

class BoardListService(private val storage: IStorage) {

    /**
     * Get detailed information of a list.
     * @param listId List identifier
     * @return BoardList object
     * @throws NotFoundException if the list is not found
     */
    fun getList(listId: Int): BoardList {
        return storage.getList(listId)
            ?: throw NotFoundException("List not found.")
    }

    /**
     * Delete a list.
     * @param listId List identifier
     * @throws NotFoundException if the list is not found
     */
    fun deleteList(listId: Int) {
        storage.getList(listId)
            ?: throw NotFoundException("List not found.")

        storage.deleteList(listId)
    }

    /**
     * Creates a new card in a list.
     * @param listId List identifier
     * @param name The task name
     * @param description The task description
     * @param dueDate The task's conclusion date
     *
     * @return Created card identifier
     * @throws NotFoundException if the list is not found
     */
    fun createListCard(
        listId: Int,
        name: String,
        description: String,
        dueDate: kotlinx.datetime.LocalDate?
    ): Int {
        val list = storage.getList(listId)
            ?: throw NotFoundException("List not found.")

        val now = LocalDate.now().toKotlinLocalDate()
        val card = storage.createCard(list.id, name, description, now, dueDate)
        return card.id
    }

    /**
     * Get the set of cards in a list.
     * @param listId List identifier
     * @param skip start position of the subsequence to return
     * @param limit length of the subsequence to return
     * @return List of Card objects
     * @throws NotFoundException if the list is not found
     */
    fun getListCards(listId: Int, skip: Int, limit: Int): List<Card> {
        storage.getList(listId)
            ?: throw NotFoundException("List not found.")
        return storage.getCardsFromList(listId, skip, limit)
    }

    /**
     * Returns true if the user associated to [token] has access to the list by [listId].
     */
    fun userOwnsList(token: String, listId: Int): Boolean {
        storage.getList(listId) ?: throw NotFoundException()

        val userId = storage.getUserIdByToken(token) ?: throw BadRequestException("Could not fetch user id")
        val boards = storage.getUserBoards(userId, DEFAULT_SKIP, DEFAULT_LIMIT)
        boards.forEach { b ->
            storage.getBoardLists(b.id, DEFAULT_SKIP, DEFAULT_LIMIT).forEach { list ->
                if (list.id == listId) {
                    return true
                }
            }
        }
        return false
    }
}
