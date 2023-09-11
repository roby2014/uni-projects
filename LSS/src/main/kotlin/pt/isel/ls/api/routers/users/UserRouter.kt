package pt.isel.ls.api.routers.users

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.security.BearerAuthSecurity
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import pt.isel.ls.api.authorizationMiddleware
import pt.isel.ls.api.getPaginationParameters
import pt.isel.ls.api.getToken
import pt.isel.ls.api.idLens
import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.UnauthorizedException
import pt.isel.ls.errors.errorHandler
import pt.isel.ls.services.UserService

/**
 * Represents a router for handling user-related HTTP requests.
 *
 * @param services the user service to use for handling user requests.
 */
class UserRouter(private val services: UserService) {

    /** The list of routes handled by this router. */
    val routes: List<ContractRoute> = listOf(

        // POST /users
        "/users" meta {
            summary = "Creates a new user"
            description = "Creates a new user, by receiving the following parameters: name & email & password"
            returning(
                Status.CREATED to "The created user token and id",
                Status.INTERNAL_SERVER_ERROR to "Error"
            )
        } bindContract POST to ::createUser,

        // GET /users/{id}
        "/users" / idLens meta {
            summary = "Get user details"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.BAD_REQUEST to "Invalid user id",
                Status.NOT_FOUND to "UserAccess-Control-Allow-Origin’ not found",
                Status.UNAUTHORIZED to "Not authorized"
            )
        } bindContract GET to { id -> { req -> getUser(req, id.toIntOrNull()) } },

        // GET /users/{id}/boards
        "/users" / idLens / "boards" meta {
            summary = "Get the list with all user available boards"
            description = "Pagination available through 'skip' & 'limit' query parameters"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "User boards",
                Status.BAD_REQUEST to "Invalid user id",
                Status.NOT_FOUND to "User not found"
            )
        } bindContract GET to { id, _ -> { req -> getUserBoards(req, id.toIntOrNull()) } },

        // POST /login
        "/login" meta {
            summary = "Login operation, returning the user token"
            description = "Body must contains the user's email and password"
            returning(Status.OK to "User token", Status.BAD_REQUEST to "Invalid user or bad credentials")
        } bindContract POST to { req -> login(req) },

        // GET /auth
        "/auth" meta {
            summary = "Returns user identifier by the user token sent in Authorization header"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "User id",
                Status.BAD_REQUEST to "Invalid token",
                Status.UNAUTHORIZED to "Not authorized"
            )
        } bindContract GET to { req -> auth(req) }
    )

    /**
     * Handles the creation of a new user.
     *
     * @param req the HTTP request containing the user information.
     * @return the HTTP response containing the created user token and id.
     */
    private fun createUser(req: Request): Response = errorHandler {
        val userJson = Json.decodeFromString<UserDTO>(req.bodyString())
        val (token, id) = services.createUser(userJson.name, userJson.email, userJson.password)

        @Serializable
        data class CreatedUserResponse(val token: String, val id: Int)
        // mapOf does not work here because id is an integer and token is a string, which means
        // it gets casted to Any and it cant be deserializable

        val jsonRes = CreatedUserResponse(token, id)

        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(jsonRes))
    }

    /**
     * Handles the retrieval of user details by user id.
     *
     * @param req the HTTP request containing the user id.
     * @param userId the id of the user to retrieve details for.
     * @return the HTTP response containing the user details.
     */
    private fun getUser(req: Request, userId: Int?): Response = errorHandler {
        userId ?: throw BadRequestException("Invalid 'id' parameter")
        val res = services.getUser(userId) // we do it here so we can first return not found instead of unauthorized

        if (services.getUserIdByToken(req.getToken()) != userId) {
            throw UnauthorizedException("No access")
        }

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handles the retrieval of all available boards of a user by user id.
     *
     * @param req the HTTP request containing the user id.
     * @param userId the id of the user to retrieve boards for.
     * @return the HTTP response containing the user's boards.
     */
    private fun getUserBoards(req: Request, userId: Int?): Response = errorHandler {
        userId ?: throw BadRequestException("Invalid 'id' parameter")

        val (skip, limit) = getPaginationParameters(req)
        val res = services.getUserBoards(userId, skip, limit) // we do it here so we can first return not found instead of unauthorized

        if (services.getUserIdByToken(req.getToken()) != userId) {
            throw UnauthorizedException("No access")
        }

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handles the retrieval of the user's token by email & password.
     *
     * @param req the HTTP request containing the user id.
     * @return the HTTP response containing the user's token.
     */
    private fun login(req: Request): Response = errorHandler {
        val loginJson = Json.decodeFromString<LoginDTO>(req.bodyString())
        val token = services.getUserToken(loginJson.email, loginJson.password)
        val jsonResponse = mapOf("token" to token)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(jsonResponse))
    }

    /**
     * Handles the retrieval of the user's identifier by Authorization token.
     *
     * @param req the HTTP request containing the Authorization header with user token.
     * @return the HTTP response containing the user's id.
     */
    private fun auth(req: Request): Response = errorHandler {
        val token = req.getToken()
        val id = services.getUserIdByToken(token)
        val jsonResponse = mapOf("id" to id)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(jsonResponse))
    }
}
