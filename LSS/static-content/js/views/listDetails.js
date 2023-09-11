import { getList, getListCards } from '../api/lists.js';
import { a, br, button, div, form, h1, input, label, li, ul } from '../dsl.js';
import { backToBtn } from '../utils.js';
import { onCreateCardSubmit, onDeleteBoardListButton } from '../events.js';

/**
 * Renders the details of a specific list.
 * 
 * @param {HTMLElement} mainContent - The main content container where the list details will be rendered.
 * @param {Object} params - An object containing parameters, with the list ID under the 'id' property.
 */
export default async function listDetails(mainContent, params) {
    const listId = params.id;
    const list = await getList(listId);
    const cards = await getListCards(listId);

    const detailsContainer = await div(
        h1(`List ${listId} details`),
        ul(
            li(`id: ${list.id}`),
            li(`name: ${list.name}`),
            li(`position: ${list.email}`),
            li(`board owner: ${list.boardId}`),
            ul({ id: "cards" },
                ...(cards.map(c => li(a({ href: `/#card/${c.id}/${listId}` }, `Card ${c.id}`))))
            )
        ),
        button({ id: "deleteBoardListButton" }, "Delete this list")
    );

    const createCardContainer = await div(
        h1("Create a new card"),
        form({ id: "createCardForm" },
            label({ for: "cardName" }, "Card name:"),
            input({ type: "text", id: "cardName", name: "cardName" }),
            br(),

            label({ for: "cardDescription" }, "Card description:"),
            input({ type: "text", id: "cardDescription", name: "cardDescription" }),
            br(),

            label({ for: "cardDueDate" }, "Card due date (YYYY-MM-DD):"),
            input({ type: "text", id: "cardDueDate", name: "cardDueDate" }),
            br(),

            button({ type: "submit" }, "Create")
        )
    );

    const deleteButton = detailsContainer.querySelector("#deleteBoardListButton");
    deleteButton.addEventListener("click", async () => onDeleteBoardListButton(listId));

    const createCardForm = createCardContainer.querySelector("#createCardForm");
    createCardForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await onCreateCardSubmit(createCardForm, listId, async (newCardID) => {
            const cardsList = detailsContainer.querySelector("#cards");
            const cardId = await newCardID;
            cardsList.appendChild(await li(a({ href: `/#card/${newCardID}/${listId}` }, `Card ${cardId}`)));
            createCardForm.reset();
        })
    });

    mainContent.replaceChildren(await backToBtn(`board/${listId}`, "board details"));
    mainContent.appendChild(detailsContainer);
    mainContent.appendChild(createCardContainer);
}