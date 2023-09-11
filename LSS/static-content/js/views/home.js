
/**
 * Renders the home page.
 * 
 * @param {HTMLElement} mainContent - The main content container where the home page will be rendered.
 */
export default async function home(mainContent) {
    const h1 = document.createElement("h1");
    const text = document.createTextNode("Home");
    h1.appendChild(text);
    mainContent.replaceChildren(h1);
}