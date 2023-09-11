import { createListInBoard, getBoard, getBoardLists } from '../api/boards.js';
import { getUserIdByToken } from '../api/users.js';
import { a, br, button, div, form, h1, input, label, li, ul } from '../dsl.js';
import { backToBtn } from '../utils.js';

/**
 * Renders the details of a board, including its name, description, and associated lists (also possible to create new lists).
 * 
 * @param {HTMLElement} mainContent - The main content container where the board details will be rendered.
 * @param {Object} params - An object containing parameters, with the board ID under the 'id' property.
 */
export default async function boardDetails(mainContent, params) {
  const boardId = params.id;
  const board = await getBoard(boardId);
  const lists = await getBoardLists(boardId);

  const detailsContainer = await div(
    h1(`Board ${boardId}`),
    ul(
      li(`id: ${board.id}`),
      li(`name: ${board.name}`),
      li(`description: ${board.description}`),
      button({ onclick: `window.location.href='/#board/${board.id}/users'` }, "Check users of this board"),
      li(`Lists of this board:`),
      ul({id: "lists"},
        ...(lists.map(l => li(a({ href: `/#list/${l.id}` }, `List ${l.id}`))))
      ),
    )
  );

  const createListContainer = await div(
    h1("Create a new list"),
    form({ id: "createBoardListForm" },
      label({ for: "listName" }, "List name:"),
      input({ type: "text", id: "listName", name: "listName" }),
      br(),
      button({ type: "submit" }, "Create")
    )
  );

  const createBoardListForm = createListContainer.querySelector("#createBoardListForm");
  createBoardListForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const listName = createBoardListForm.elements.listName.value;
    const newListID = await createListInBoard(boardId, listName);
    const listsUl = detailsContainer.querySelector("#lists");
    listsUl.appendChild(await li(a({ href: `/#list/${newListID}` }, `List ${newListID}`)));
    createBoardListForm.reset();
  });

  const userId = getUserIdByToken(localStorage.getItem("token"));
  mainContent.replaceChildren(await backToBtn(`user/${userId}/boards`, "boards"));
  mainContent.appendChild(detailsContainer);
  mainContent.appendChild(createListContainer);
}