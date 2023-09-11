import { getBoardUsers } from '../api/boards.js';
import { div, h1, h3, ul } from '../dsl.js';
import { backToBtn } from '../utils.js';

/**
 * Renders the users associated with a specific board.
 * 
 * @param {HTMLElement} mainContent - The main content container where the board users will be rendered.
 * @param {Object} params - An object containing parameters, with the board ID under the 'id' property.
 */
export default async function boardUsers(mainContent, params) {
  const boardId = params.id;
  const users = await getBoardUsers(boardId);

  const boardContainer = await div(
    h1(`Users of board ${boardId}`),
    div(
      ul(
        ...(users.map(u => h3(`${u.id} - ${u.name} (${u.email})`)))
      )
    )
  );

  mainContent.replaceChildren(await backToBtn(`board/${boardId}`, "board details"));
  mainContent.appendChild(boardContainer);
}
