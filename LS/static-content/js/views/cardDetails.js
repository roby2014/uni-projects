import { getCard } from '../api/cards.js';
import { onMoveCardSubmit } from '../events.js';
import { br, button, div, form, h1, h2, input, label, li, ul } from '../dsl.js';
import { backToBtn } from '../utils.js';

/**
 * Renders the details of a specific card. Moving it is also possible.
 * 
 * @param {HTMLElement} mainContent - The main content container where the card details will be rendered.
 * @param {Object} params - An object containing parameters, with the card ID under the 'id' property and the list ID under the 'listId' property.
 */
export default async function cardDetails(mainContent, params) {
  const cardId = params.id;
  const listId = params.listId;
  const card = await getCard(cardId)

  const detailsContainer = await div(
    h1(`Card ${cardId} details`),
    ul(
      li(`id: ${card.id}`),
      li(`name: ${card.name}`),
      li(`description: ${card.description}`),
      li(`creationDate: ${card.creationDate}`),
      li(`conclusionDate: ${card.conclusionDate}`)
    ),
    div(
      h2("Move card"),
      form(
        label({ for: "lid" }, "Destination list"),
        input({ type: "text", id: "lid", name: "lid" }),
        br(),

        label({ for: "lid" }, "New index"),
        input({ type: "text", id: "idx", name: "idx" }),
        br(),

        button({ type: "submit" }, "Move")
      )
    )
  );

  detailsContainer.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (await onMoveCardSubmit(cardId, listId)) {
      window.location.href = `/#list/${listId}`;
    }
  });

  mainContent.replaceChildren(await backToBtn(`list/${listId}`, "list details"));
  mainContent.appendChild(detailsContainer);
}