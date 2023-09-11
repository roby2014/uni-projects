import { getUser } from '../api/users.js';
import { div, h1, li, ul } from '../dsl.js';
import { backToBtn, getUserId, isLogged } from '../utils.js';

export default async function userDetails(mainContent) {
  if (!isLogged()) {
    window.location.href = `/#home`;
    return;
  }

  const userId = getUserId();
  const user = await getUser(userId);

  const detailsContainer = await div(
    h1("My details"),
    ul(
      li(`id: ${user.id}`),
      li(`name: ${user.name}`),
      li(`email: ${user.email}`)
    )
  );

  mainContent.replaceChildren(await backToBtn("home", "home"));
  mainContent.appendChild(detailsContainer);
}