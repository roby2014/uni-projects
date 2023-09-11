import { API_BASE_URL } from '../utils.js';

/**
 * Retrieves a list by its ID.
 * 
 * @param {string} listId - The ID of the list to retrieve.
 * @returns {Promise<Object>} - A Promise that resolves to the retrieved list.
 */
export async function getList(listId) {
    const listResponse = await fetch(`${API_BASE_URL}/lists/${listId}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const listData = await listResponse.json();
    return listData;
}

/**
 * Retrieves the cards of a list by its ID.
 * 
 * @param {string} listId - The ID of the list to retrieve cards from.
 * @returns {Promise<Array<Object>>} - A Promise that resolves to an array of cards belonging to the list.
 */
export async function getListCards(listId) {
    const cardsResponse = await fetch(`${API_BASE_URL}/lists/${listId}/cards`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const cardsData = await cardsResponse.json();
    return cardsData;
}

/**
 * Deletes a list by its ID.
 * 
 * @param {string} listId - The ID of the list to delete.
 */
export async function deleteList(listId) {
    await fetch(`${API_BASE_URL}/lists/${listId}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
}

/**
 * Creates a new card in a list.
 * 
 * @param {string} listId - The ID of the list to create the card in.
 * @param {Object} body - The body containing the data for the new card.
 * @returns {Promise<string>} - A Promise that resolves to the ID of the newly created card.
 */
export async function createCardInList(listId, body) {
    const response = await fetch(`${API_BASE_URL}/lists/${listId}/cards`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });
    const newCardID = await response.json();
    return newCardID;
}