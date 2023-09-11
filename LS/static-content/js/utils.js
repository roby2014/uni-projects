import { button, div } from "./dsl.js";

export const API_BASE_URL = "http://localhost:9000/api"

/**
 * Simple wrapper function that creates a "Back to [buttonDisplayText]" button, where it redirects to [whereHref].
 * 
 * @param {*} whereHref Where to redirect the user (only the path after the #).
 * @param {*} buttonDisplayText Display text (after "Back to ").
 * @returns the button HTML element.
 */
export async function backToBtn(whereHref, buttonDisplayText) {
  const btn = await div(
    button({onclick: `window.location.href='/#${whereHref}'`}, `Back to ${buttonDisplayText}`)
  );
  return btn;
}

/**
 * Wrapper for checking if user is logged in.
 * 
 * @returns true if user is logged in (checks local storage for token and id), false otherwise
 */
export function isLogged() {
  return localStorage.getItem("token") != undefined && localStorage.getItem("id") != undefined;
}

/**
 * Wrapper for getting user's id from local storage.
 * 
 * @returns stored user id from local storage.
 */
export function getUserId() {
  return localStorage.getItem("id");
}