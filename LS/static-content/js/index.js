import router from "./router.js";
import views from "./views.js";

window.addEventListener('load', loadHandler);
window.addEventListener('hashchange', hashChangeHandler);

/**
 * Associates all routes path's to their view handler/callback.
 */
function loadHandler() {
    router.addRouteHandler("home", views.home);
    router.addRouteHandler("login", views.login);
    router.addRouteHandler("logout", views.logout);
    router.addRouteHandler("signup", views.signUp);
    router.addRouteHandler("user/:id", views.userDetails);
    router.addRouteHandler("user/:id/boards", views.userBoards);
    router.addRouteHandler("board/:id", views.boardDetails);
    router.addRouteHandler("board/:id/users", views.boardUsers);
    router.addRouteHandler("list/:id", views.listDetails);
    router.addRouteHandler("card/:id/:listId", views.cardDetails);

    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home");

    hashChangeHandler();
}

function hashChangeHandler() {
    const mainContent = document.getElementById("mainContent");
    const routeUrl = window.location.hash.replace("#", "");
    const routeObj = router.getRouteObj(routeUrl);
    const handler = routeObj.handler;
    const params = routeObj.params;
    handler(mainContent, params);
}
