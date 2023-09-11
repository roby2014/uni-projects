import { onLoginSubmit } from '../events.js';
import { button, form, h1, input, label } from '../dsl.js';
import { isLogged } from '../utils.js';

/**
 * Renders the login page.
 * 
 * @param {HTMLElement} mainContent - The main content container where the login page will be rendered.
 */
export default async function login(mainContent) {
    if (isLogged()) {
        window.location.href = `/#home`;
        return;
    }

    const loginForm = await form(
        h1("Login"),
        label({ for: "email" }, "Email"),
        input({ type: "email", id: "email", name: "email", required: true }),
        label({ for: "password" }, "Password"),
        input({ type: "password", id: "password", name: "password", required: true }),
        button({ type: "submit" }, "Login")
    );

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        if (await onLoginSubmit(loginForm)) {
            window.location.href = `/#home`;
        }
    });

    mainContent.replaceChildren(loginForm);
}