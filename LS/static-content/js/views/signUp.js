
import { onSignupSubmit } from '../events.js';
import { isLogged } from '../utils.js';

export default async function signUp(mainContent) {
    if (isLogged()) {
        window.location.href = `/#home`;
        return;
    }

    const signUpForm = document.createElement("form");
    signUpForm.innerHTML = `
      <h1>Sign Up</h1>
      <label for="name">Name:</label>
      <input type="text" id="name" name="name" required>
      <label for="email">Email:</label>
      <input type="email" id="email" name="email" required>
      <label for="password">Password:</label>
      <input type="password" id="password" name="password" required>
      <button type="submit">Sign Up</button>
    `;

    signUpForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        if (await onSignupSubmit(signUpForm)) {
            window.location.href = `/#home`;
        }
    });

    mainContent.replaceChildren(signUpForm);
}

