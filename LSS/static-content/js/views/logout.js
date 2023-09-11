import { isLogged } from "../utils.js";

export default async function logout(mainContent) {
  if (!isLogged()) {
    window.location.href = `/#home`;
    return;
  }

  localStorage.removeItem("token");
  localStorage.removeItem("id");
  alert("Logged out!");
  window.location.href = `/#home`;
}