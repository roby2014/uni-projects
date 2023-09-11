import { API_BASE_URL } from '../utils.js';

/**
 * Retrieves a board by its ID.
 * 
 * @param {string} boardId - The ID of the board to retrieve.
 * @returns {Promise<Object>} - A Promise that resolves to the retrieved board.
 */
export async function getBoard(boardId) {
    const boardResponse = await fetch(`${API_BASE_URL}/boards/${boardId}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const board = await boardResponse.json();
    return board;
}

/**
 * Retrieves the lists of a board by its ID.
 * 
 * @param {string} boardId - The ID of the board to retrieve lists from.
 * @returns {Promise<Array<Object>>} - A Promise that resolves to an array of lists belonging to the board.
 */
export async function getBoardLists(boardId) {
    const listsResponse = await fetch(`${API_BASE_URL}/boards/${boardId}/lists`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const lists = await listsResponse.json();
    return lists;
}

/**
 * Creates a new list in a board.
 * 
 * @param {string} boardId - The ID of the board to create the list in.
 * @param {string} listName - The name of the list to create.
 * @returns {Promise<string>} - A Promise that resolves to the ID of the newly created list.
 */
export async function createListInBoard(boardId, listName) {
    const response = await fetch(`${API_BASE_URL}/boards/${boardId}/lists?name=${listName}`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });

    const newListID = await response.json();
    return newListID;
}

/**
 * Retrieves the users associated with a board.
 * 
 * @param {string} boardId - The ID of the board to retrieve users from.
 * @returns {Promise<Array<Object>>} - A Promise that resolves to an array of users associated with the board.
 */
export async function getBoardUsers(boardId) {
    const usersResponse = await fetch(`${API_BASE_URL}/boards/${boardId}/users`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        },
    });
    const usersData = await usersResponse.json();
    return usersData;
}

/**
 * Creates a new board.
 * 
 * @param {string} boardName - The name of the new board.
 * @param {string} boardDescription - The description of the new board.
 * @returns {Promise<string>} - A Promise that resolves to the ID of the newly created board.
 */
export async function createBoard(boardName, boardDescription) {
    const response = await fetch(`${API_BASE_URL}/boards`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify({ name: boardName, description: boardDescription })
    });
    const newBoardID = await response.json();
    return newBoardID;
}

/**
 * Adds a user to a board.
 * 
 * @param {string} userId - The ID of the user to add.
 * @param {string} newBoardID - The ID of the board to add the user to.
 */
export async function addUserToBoard(userId, newBoardID) {
    await fetch(`${API_BASE_URL}/boards/${newBoardID}/users?userId=${userId}`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
}

/**
 * Fetches boards based on search data.
 * 
 * @param {string} data - The search data used to fetch boards.
 * @returns {Promise<Array<Object>>} - A Promise that resolves to an array of boards matching the search data.
 */
export async function fetchBoards(data) {
    const response = await fetch(`${API_BASE_URL}/search/boards?data=${encodeURIComponent(data)}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const boards = await response.json();
    return boards;
};