package pt.isel.ls.services

import pt.isel.ls.api.DEFAULT_LIMIT
import pt.isel.ls.api.DEFAULT_SKIP
import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.NotFoundException
import pt.isel.ls.models.Card
import pt.isel.ls.storage.IStorage

class CardService(private val storage: IStorage) {

    /**
     * Get the detailed information of a card.
     *
     * @param cardId The card identifier.
     * @return The card object.
     * @throws NotFoundException If the card is not found.
     */
    fun getCard(cardId: Int): Card {
        return storage.getCard(cardId)
            ?: throw NotFoundException("Card not found.")
    }

    /**
     * Moves a card to a new list.
     *
     * @param cardId The card identifier.
     * @param listId The identifier of the list to move the card to.
     * @param newIndex The new index for the card in the destination list.
     * @throws NotFoundException If either the card or the list are not found.
     */
    fun moveCard(cardId: Int, listId: Int, newIndex: Int) {
        if (newIndex < 0) {
            throw BadRequestException("'newIndex' must be a valid index (>0).")
        }

        storage.getCard(cardId)
            ?: throw NotFoundException("Card not found.")

        storage.getList(listId)
            ?: throw NotFoundException("List not found.")

        storage.moveCard(cardId, listId, newIndex)
    }

    /**
     * Deletes a card.
     *
     * @param cardId The card identifier.
     * @throws NotFoundException If either the card or the list are not found.
     */
    fun deleteCard(cardId: Int) {
        storage.getCard(cardId)
            ?: throw NotFoundException("Card not found.")

        storage.deleteCard(cardId)
    }

    /**
     * Returns true if the user associated to [token] has access to the card by [cardId].
     */
    fun userOwnsCard(token: String, cardId: Int): Boolean {
        storage.getCard(cardId) ?: throw NotFoundException()

        val listId = storage.getListIdFromCard(cardId)
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
