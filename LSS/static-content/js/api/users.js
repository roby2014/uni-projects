import { API_BASE_URL } from "../utils.js";

/**
 * Retrieves a user by their ID.
 * 
 * @param {string} userId - The ID of the user to retrieve.
 * @returns {Promise<Object>} - A Promise that resolves to the retrieved user.
 */
export async function getUser(userId) {
    const res = await fetch(`${API_BASE_URL}/users/${userId}`, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    const user = await res.json();
    return user;
}

/**
 * Logs in a user with the provided email and password.
 * 
 * @param {string} email - The email of the user.
 * @param {string} password - The password of the user.
 * @returns {Promise<Response>} - A Promise that resolves to the response of the login operation.
 */
export async function loginUser(email, password) {
    return await fetch(`${API_BASE_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });
}

/**
 * Retrieves the user ID associated with the provided token.
 * 
 * @param {string} token - The authentication token.
 * @returns {Promise<string>} - A Promise that resolves to the user ID.
 */
export async function getUserIdByToken(token) {
    const res = await fetch(`${API_BASE_URL}/auth`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    const json = await res.json();
    return json.id;
}

/**
 * Creates a new user with the provided name, email, and password.
 * 
 * @param {string} name - The name of the user.
 * @param {string} email - The email of the user.
 * @param {string} password - The password of the user.
 * @returns {Promise<Response>} - A Promise that resolves to the response of the create user operation.
 */
export async function createUser(name, email, password) {
    return await fetch(`${API_BASE_URL}/users`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password }),
    });
}

/**
 * Retrieves the boards associated with a user.
 * 
 * @param {string} userId - The ID of the user to retrieve boards for.
 * @param {string} token - The authentication token.
 * @returns {Promise<Array<Object>>} - A Promise that resolves to an array of boards associated with the user.
 */
export async function getUserBoards(userId, token) {
    const boardsResponse = await fetch(`${API_BASE_URL}/users/${userId}/boards`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    const boardsData = await boardsResponse.json();
    return boardsData;
}
