import { createBoard } from "./api/boards.js";
import { moveCard } from "./api/cards.js";
import { createCardInList, deleteList } from "./api/lists.js";
import { getUserIdByToken, loginUser, createUser } from "./api/users.js";
import { getUserId } from "./utils.js";

// button callbacks, forms submit, etc...

/**
 * Called when the client tries to delete a card by the form.
 * 
 * @param {*} listId List identifier from where the card is. 
 */
export async function onDeleteBoardListButton(listId) {
    const confirmation = confirm("Are you sure you want to delete this list?");
    if (confirmation) {
        try {
            await deleteList(listId);
            alert("List deleted successfully");
            window.location.href = `/#user/${getUserId()}/boards`;
        } catch (error) {
            alert("An error occurred while deleting the list. Please try again later.");
            console.error(error);
        }
    }
}

/**
 * Called when the client tries to create a card by the form.
 * 
 * @param {*} form Create card form form. 
 * @param {*} listId List identifier where the card will be. 
 * @param {*} updateCardsListFn This lambda function will be called after the API returns 
 * the created new card identifier, passing it as a parameter. 
 */
export async function onCreateCardSubmit(form, listId, updateCardsListFn) {
    const cardName = form.elements.cardName.value;
    const cardDescription = form.elements.cardDescription.value;
    const cardDueDate = form.elements.cardDueDate.value;

    const bodyObj = { name: cardName, description: cardDescription }
    if (cardDueDate != '') {
        bodyObj.dueDate = cardDueDate;
    }

    const newCardID = await createCardInList(listId, bodyObj)
    await updateCardsListFn(newCardID);
}

/**
 * Called when the client tries to log in.
 * 
 * @param {*} form Login form. 
 * @returns true if login was successful, false otherwise.
 */
export async function onLoginSubmit(form) {
    const formData = new FormData(form);
    const email = formData.get("email");
    const password = formData.get("password");
    const response = await loginUser(email, password);

    if (!response.ok) {
        const { errorObj, errorDescription } = await response.json();
        alert(errorDescription);
        return false;
    }

    const { token } = await response.json();
    const userId = await getUserIdByToken(token);
    localStorage.setItem("token", token);
    localStorage.setItem("id", userId);
    alert("Login successful!");
    return true;
}

/**
 * Called when the client signups a new account.
 * 
 * @param {*} form Signup form. 
 * @returns true if signup was successful, false otherwise.
 */
export async function onSignupSubmit(form) {
    const formData = new FormData(form);
    const name = formData.get("name");
    const email = formData.get("email");
    const password = formData.get("password");
    const signup = await createUser(name, email, password);

    if (signup.status != 201) {
        const { errorObj, errorDescription } = await signup.json();
        alert(errorDescription);
        return false;
    }

    const { token, id } = await signup.json();
    localStorage.setItem("token", token);
    localStorage.setItem("id", id);
    alert("Sign up successful!");
    return true;
}

/**
 * Called when the client moves a card using the form. 
 * 
 * @param {*} cardId Card identifier to move.
 * @param {*} listId List identifier destination.
 * @returns true if card was moved correctly, false otherwise.
 */
export async function onMoveCardSubmit(cardId, listId) {
    const lid = document.getElementById("lid").value;
    const idx = document.getElementById("idx").value;
    const moved = await moveCard(cardId, lid, idx);

    if (moved.status == 200) {
        alert("Card moved");
        return true;
    }

    // not ok, api should return an error
    const jsonData = await moved.json();
    const errorCode = jsonData.errorCode;
    const errorMsg = `(${errorCode} error) ${jsonData.errorDescription}`;
    alert(errorMsg);
}

/**
 * Called when client creates a board using the form.
 * 
 * @param {*} form Create board form.
 * @param {*} createdBoardFn This lambda function will be called after the API returns 
 * the created new board identifier, passing it as a parameter, and the board name is also passed as 2nd parameter.
 */
export async function onCreateBoardSubmit(form, createdBoardFn) {
    // create board
    const boardName = form.elements.boardName.value;
    const boardDescription = form.elements.boardDescription.value;
    const newBoardID = await createBoard(boardName, boardDescription);
    createdBoardFn(newBoardID, boardName);
}