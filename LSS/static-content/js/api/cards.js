import { API_BASE_URL } from '../utils.js';

/**
 * Retrieves a card by its ID.
 * 
 * @param {string} cardId - The ID of the card to retrieve.
 * @returns {Promise<Object>} - A Promise that resolves to the retrieved card.
 */
export async function getCard(cardId) {
    const cardResponse = await fetch(`${API_BASE_URL}/cards/${cardId}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const cardData = await cardResponse.json();
    return cardData;
}

/**
 * Retrieves the cards of a list by its ID.
 * 
 * @param {string} listId - The ID of the list to retrieve cards from.
 * @returns {Promise<Array<Object>>} - A Promise that resolves to an array of cards belonging to the list.
 */
export async function getCards(listId) {
    const listResponse = await fetch(`${API_BASE_URL}/lists/${listId}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const listData = await listResponse.json();
    return listData;
}

/**
 * Moves a card to a different list and index.
 * 
 * @param {string} cardId - The ID of the card to move.
 * @param {string} lid - The ID of the list to move the card to.
 * @param {number} idx - The index at which to place the card in the new list.
 * @returns {Promise<Response>} - A Promise that resolves to the response of the move operation.
 */
export async function moveCard(cardId, lid, idx) {
    return await fetch(`${API_BASE_URL}/cards/${cardId}?listId=${lid}&newIndex=${idx}`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
}