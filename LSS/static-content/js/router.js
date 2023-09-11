/**
 * Routes array. Will store all the SPA possible route objects (path, handler)
 */
const routes = []

/**
 * The default not found route handler function, which is used if no other route handler
 * function matches the current route URL.
 */
let notFoundRouteHandler = () => { throw "Route handler for unknown routes not defined" };

/**
 * Adds a new route handler function for the given path. When a route URL matches this path..
 *
 * @param {string} path - The route path to match, which may include dynamic parameters in the format ":paramName".
 * @param {function} handler - The route handler function to call when a route URL matches the path.
 */
function addRouteHandler(path, handler) {
    routes.push({ path, handler });
}

/**
 * Gets the route object for the given route URL. The route object contains the matching route handler function
 * and the values of any dynamic parameters in the route URL.
 *
 * @param {string} routeUrl - The route URL to get the route object for.
 *
 * @returns {object} An object with two properties: `handler`, which is the matching route handler function, and
 * `params`, which is an object containing the values of any dynamic parameters in the route URL.
 */
function getRouteObj(routeUrl) {
    let handler = notFoundRouteHandler;
    let params = {};

    routes.forEach(r => {
        const matchedRoute = matchRoute(r.path, routeUrl);
        if (matchedRoute.match) {
            handler = r.handler;
            params = matchedRoute.params;
            return;
        }
    });

    return { handler, params };
}

/**
 * Sets the default not found route handler function.
 *
 * @param {function} notFoundRH - The not found route handler function to use as the default.
 */
function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH;
}

/**
 * Utility function to check if a given route URL matches a given route path, which can include
 * dynamic parameters. If the route URL matches the route path, returns an object with a boolean
 * value of `true` for the `match` property, indicating a successful match, and an object for the
 * `params` property with the dynamic parameter values from the route URL.
 *
 * @param {string} routePath - The original route path to be matched, which may include dynamic
 * parameters in the format ":paramName".
 * @param {string} routeUrl - The route URL to be matched against the route path.
 *
 * @returns {object} An object with two properties: `match`, which is a boolean value indicating
 * whether the route URL matches the route path, and `params`, which is an object containing the
 * values of any dynamic parameters in the route URL.
 *
 * @example
 *
 * // Returns { match: true, params: { id: '4' } }
 * const result1 = matchRoute('/user/:id', '/user/4');
 * 
 * // Returns { match: true, params: { userId: '123', boardId: '456' } }
 * const result2 = matchRoute('/users/:userId/boards/:boardId', '/users/123/boards/456');
 */
function matchRoute(routePath, routeUrl) {
    const pathParts = routePath.split("/");
    const urlParts = routeUrl.split("/");
    const params = {};
    let match = true;

    if (pathParts.length !== urlParts.length) {
        match = false;
    } else {
        for (let i = 0; i < pathParts.length; i++) {
            const pathPart = pathParts[i];
            const urlPart = urlParts[i];
            if (pathPart.charAt(0) === ":") {
                const paramName = pathPart.substring(1);
                params[paramName] = urlPart;
            } else if (pathPart !== urlPart) {
                match = false;
                break;
            }
        }
    }

    return { match, params };
}

const router = {
    addRouteHandler,
    getRouteObj,
    addDefaultNotFoundRouteHandler
}

export default router