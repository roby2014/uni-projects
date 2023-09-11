import { API_BASE_URL, backToBtn, getUserId, isLogged } from '../utils.js';
import { a, br, button, div, form, h1, h2, h3, input, label, li, ul } from '../dsl.js';
import { onCreateBoardSubmit } from '../events.js';
import { addUserToBoard, fetchBoards } from '../api/boards.js';
import { getUserBoards } from '../api/users.js';

export default async function userBoards(mainContent) {
  if (!isLogged()) {
    window.location.href = `/#home`;
    return;
  }

  const userId = getUserId();
  const boards = await getUserBoards(userId, localStorage.getItem("token"));

  const myBoardsContainer = await div(
    h1("My boards"),
    ...boards.map(b =>
      div(
        h3(
          a({ href: `/#board/${b.id}` }, `Board ${b.id}: ${b.name}`)
        )
      )
    )
  );

  const createBoardContainer = await div(
    h1("Create a new board"),
    form({ id: "createBoardForm" },
      label({ for: "boardName" }, "Board name:"),
      input({ type: "text", id: "boardName", name: "boardName" }),
      br(),

      label({ for: "boardDescription" }, "Board description:"),
      input({ type: "text", id: "boardDescription", name: "boardDescription" }),
      br(),

      button({ type: "submit" }, "Create")
    )
  );

  const createBoardForm = createBoardContainer.querySelector("#createBoardForm");
  createBoardForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    onCreateBoardSubmit(createBoardForm, async (newBoardID, boardName) => {
      newBoardID = await newBoardID;
      await addUserToBoard(userId, newBoardID);

      const boardLink = await div(
        h3(a({ href: `/#board/${newBoardID}` }, `Board ${newBoardID}: ${boardName}`))
      );
      myBoardsContainer.append(boardLink);
      createBoardForm.reset();
    });
  });

  mainContent.replaceChildren(await backToBtn("home", "home"));
  mainContent.appendChild(myBoardsContainer);
  mainContent.appendChild(createBoardContainer);

  searchBoards(mainContent);
}

async function searchBoards(mainContent, params) {
  const searchFormContainer = await div(
    h1("Search boards"),
    form(
      label({ for: "search-input" }, "Data"),
      input({ type: "text", id: "search-input", name: "search-input" }),
      button({ type: "submit" }, "Search")
    )
  );

  const searchResults = document.createElement("div");

  // FIXME: use the html dsl render approach
  const displayBoards = async (boards) => {
    if (boards.length === 0) {
      searchResults.textContent = 'No boards found.';
    } else {
      searchResults.innerHTML = `
        <h2>Found ${boards.length} board(s):</h2>
        <ul>
          <li><a href="/#board/${board.id}">${board.name}</a></li>
        </ul>
      `;
    }
  };

  searchFormContainer.addEventListener('submit', async (event) => {
    event.preventDefault();
    const data = document.getElementById("search-input").value.trim();

    if (data.length === 0)
      return;

    const boards = await fetchBoards(data);
    displayBoards(boards);
  });

  mainContent.appendChild(searchFormContainer);
  mainContent.appendChild(searchResults);
}