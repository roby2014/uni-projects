package pt.isel.ls.api.routers.boards

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.contract.ContractRoute
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.contract.security.BearerAuthSecurity
import org.http4k.core.Method
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
import pt.isel.ls.services.BoardService

/**
 * Router for handling HTTP requests related to boards.
 *
 * @param services the board service used to handle the requests.
 */
class BoardRouter(private val services: BoardService) {

    /** The list of routes handled by this router. */
    val routes: List<ContractRoute> = listOf(

        // POST /boards/{id}/users
        "/boards" / idLens / "users" meta {
            summary = "Add a user to the board"
            description = "Query parameters: 'userId' (user identifier that will be added to the board)"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Success",
                Status.BAD_REQUEST to "No userId query parameter",
                Status.NOT_FOUND to "User/board not found or invalid board id"
            )
        } bindContract Method.POST to { id, _ -> { req -> addUserToBoard(req, id.toIntOrNull()) } },

        // POST /boards
        "/boards" meta {
            summary = "Creates a new board"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.CREATED to "Created board id"
            )
        } bindContract Method.POST to ::createBoard,

        // GET /boards/{id}
        "/boards" / idLens meta {
            summary = "Get the detailed information of a board"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Board",
                Status.NOT_FOUND to "Board not found or invalid board id"
            )
        } bindContract Method.GET to { id -> { req -> getBoard(req, id.toIntOrNull()) } },

        // POST /boards/{id}/lists
        "/boards" / idLens / "lists" meta {
            summary = "Creates a new list on a board"
            security = BearerAuthSecurity(authorizationMiddleware)
            description = "Query parameters: 'name' (list name)"
            returning(
                Status.CREATED to "Created list id",
                Status.BAD_REQUEST to "No 'name' query parameter",
                Status.NOT_FOUND to "Board not found or invalid board id"
            )
        } bindContract Method.POST to { id, _ -> { req -> createBoardList(req, id.toIntOrNull()) } },

        // GET /boards/{id}/lists
        "/boards" / idLens / "lists" meta {
            summary = "Get the lists of a board"
            security = BearerAuthSecurity(authorizationMiddleware)
            description = "Pagination available through 'skip' & 'limit' query parameters"
            returning(
                Status.OK to "Board lists",
                Status.NOT_FOUND to "Board not found or invalid board id"
            )
        } bindContract Method.GET to { id, _ -> { req -> getBoardLists(req, id.toIntOrNull()) } },

        // GET /boards/{id}/users
        "/boards" / idLens / "users" meta {
            summary = "Get the list of users of a board"
            description = "Pagination available through 'skip' & 'limit' query parameters"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Users lists",
                Status.NOT_FOUND to "Invalid board id"
            )
        } bindContract Method.GET to { id, _ -> { req -> getBoardUsersList(req, id.toIntOrNull()) } },

        // GET /search/boards
        "/search/boards" meta {
            summary = "Searches boards"
            description =
                "Query parameters: 'data' (data that the server will search to boards). The api will look for names, descriptions or ids that match this data"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Boards found",
                Status.UNAUTHORIZED to "No auth token found, the token is needed to only fetch the boards you have access"
            )
        } bindContract Method.GET to ::searchBoards

    )

    /**
     * Handler for adding a user to a board.
     *
     * @param req the HTTP request containing the query parameter 'userId'.
     * @param boardId the ID of the board the user will be added to.
     * @return the HTTP response.
     * @throws BadRequestException if the 'userId' query parameter is invalid or not present.
     */
    private fun addUserToBoard(req: Request, boardId: Int?): Response = errorHandler {
        boardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userInBoard(req.getToken(), boardId)) {
            throw UnauthorizedException("Only users from this board can add users")
        }

        // Parse the 'userId' query parameter
        val userId = req.query("userId")?.toIntOrNull()
            ?: throw BadRequestException("Invalid query parameter 'userId'")

        // Call the board service to add the user to the board
        services.addUserToBoard(userId, boardId)

        // Return a success response
        Response(Status.OK)
    }

    /**
     * Handler for creating a new board.
     *
     * @param req the HTTP request containing the body string with board fields.
     * @return the HTTP response containing the board identifier.
     */
    private fun createBoard(req: Request): Response = errorHandler {
        val boardJson = Json.decodeFromString<BoardDTO>(req.bodyString())
        val res = services.createBoard(boardJson.name, boardJson.description)

        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handler for getting board details.
     *
     * @param req the HTTP request.
     * @param boardId the ID of the board .
     * @return the HTTP response containing the board object.
     */
    private fun getBoard(req: Request, boardId: Int?): Response = errorHandler {
        boardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userInBoard(req.getToken(), boardId)) {
            throw UnauthorizedException("Only users from this board can view the board")
        }

        val res = services.getBoard(boardId)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handler for creating a new list in board.
     *
     * @param req the HTTP request.
     * @param boardId the ID of the board.
     * @return the HTTP response containing the board list identifier.
     * @throws BadRequestException if the 'name' query parameter is invalid or not present.
     */
    private fun createBoardList(req: Request, boardId: Int?): Response = errorHandler {
        boardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userInBoard(req.getToken(), boardId)) {
            throw UnauthorizedException("Only users from this board can create lists on the board")
        }

        val listName = req.query("name")
            ?: throw BadRequestException("Invalid query parameter 'name'")

        val res = services.createBoardList(boardId, listName)

        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handler for getting board's lists.
     *
     * @param req the HTTP request.
     * @param boardId the ID of the board.
     * @return the HTTP response containing the list of board lists objects.
     */
    private fun getBoardLists(req: Request, boardId: Int?): Response = errorHandler {
        boardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userInBoard(req.getToken(), boardId)) {
            throw UnauthorizedException("Only users from this board can view the board lists")
        }

        val (skip, limit) = getPaginationParameters(req)
        val res = services.getBoardLists(boardId, skip, limit)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handler for getting board's user list.
     *
     * @param req the HTTP request.
     * @param boardId the ID of the board.
     * @return the HTTP response containing the list of board lists objects.
     */
    private fun getBoardUsersList(req: Request, boardId: Int?): Response = errorHandler {
        boardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userInBoard(req.getToken(), boardId)) {
            throw UnauthorizedException("Only users from this board can view the board users")
        }

        val (skip, limit) = getPaginationParameters(req)
        val res = services.getBoardUsersList(boardId, skip, limit)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handler for searching a board.
     *
     * @param req the HTTP request.
     * @return the HTTP response containing the list of boards founds.
     */
    private fun searchBoards(req: Request): Response = errorHandler {
        val data = req.query("data")
            ?: throw BadRequestException("Invalid query parameter 'data'")

        val res = services.searchBoards(data)

        // filter only the boards that user has access
        val filtered = res.filter { services.userInBoard(req.getToken(), it.id) }

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(filtered))
    }
}
