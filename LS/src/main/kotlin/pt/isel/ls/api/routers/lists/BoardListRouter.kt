package pt.isel.ls.api.routers.lists

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
import pt.isel.ls.api.routers.cards.CardDTO
import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.UnauthorizedException
import pt.isel.ls.errors.errorHandler
import pt.isel.ls.services.BoardListService

/**
 * Represents a router for handling lists-related HTTP requests.
 *
 * @param services the user service to use for handling board lists requests.
 */
class BoardListRouter(private val services: BoardListService) {

    /** The list of routes handled by this router. */
    val routes: List<ContractRoute> = listOf(

        // GET /lists/{id}
        "/lists" / idLens meta {
            summary = "Get detailed information of a list"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Lists information",
                Status.BAD_REQUEST to "Invalid list id",
                Status.NOT_FOUND to "List not found"
            )
        } bindContract Method.GET to { id -> { req -> getList(req, id.toIntOrNull()) } },

        // POST /lists/{id}/cards
        "/lists" / idLens / "cards" meta {
            summary = "Creates a new card in a list"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Card id",
                Status.BAD_REQUEST to "Invalid list id",
                Status.NOT_FOUND to "List not found"
            )
        } bindContract Method.POST to { id, _ -> { req -> createListCard(req, id.toIntOrNull()) } },

        // GET /lists/{id}/cards
        "/lists" / idLens / "cards" meta {
            summary = "Get the set of cards in a list"
            description = "Pagination available through 'skip' & 'limit' query parameters"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Cards from list",
                Status.BAD_REQUEST to "Invalid list id",
                Status.NOT_FOUND to "List not found"
            )
        } bindContract Method.GET to { id, _ -> { req -> getListCards(req, id.toIntOrNull()) } },

        // DELETE /lists/{id}
        "/lists" / idLens meta {
            summary = "Deletes a list"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "",
                Status.BAD_REQUEST to "Invalid list id",
                Status.NOT_FOUND to "List not found"
            )
        } bindContract Method.DELETE to { id -> { req -> deleteList(req, id.toIntOrNull()) } }
    )

    /**
     * Handler for getting a board list.
     *
     * @param req the HTTP request.
     * @param listId the ID of the board list.
     * @return the HTTP response containing the board list object.
     */
    private fun getList(req: Request, listId: Int?): Response = errorHandler {
        listId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsList(req.getToken(), listId)) {
            throw UnauthorizedException("No access")
        }

        val res = services.getList(listId)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handler for deleting a board list.
     *
     * @param req the HTTP request.
     * @param listId the ID of the board list.
     * @return the HTTP response containing the board list object.
     */
    private fun deleteList(req: Request, listId: Int?): Response = errorHandler {
        listId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsList(req.getToken(), listId)) {
            throw UnauthorizedException("No access")
        }

        services.deleteList(listId)
        Response(Status.OK)
    }

    /**
     * Handler for creating a board list card.
     *
     * @param req the HTTP request containing the body with card object fields.
     * @param listId the ID of the board list.
     * @return the HTTP response containing the created card identifier.
     */
    private fun createListCard(req: Request, listId: Int?): Response = errorHandler {
        listId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsList(req.getToken(), listId)) {
            throw UnauthorizedException("No access")
        }

        val cardJson = Json.decodeFromString<CardDTO>(req.bodyString())
        val res = services.createListCard(listId, cardJson.name, cardJson.description, cardJson.dueDate)

        Response(Status.CREATED).body(Json.encodeToString(res))
    }

    /**
     * Handler for getting a board list cards.
     *
     * @param req the HTTP request containing the body with card object fields.
     * @param listId the ID of the board list.
     * @return the HTTP response containing the list of card objects.
     */
    private fun getListCards(req: Request, listId: Int?): Response = errorHandler {
        listId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsList(req.getToken(), listId)) {
            throw UnauthorizedException("No access")
        }

        val (skip, limit) = getPaginationParameters(req)
        val res = services.getListCards(listId, skip, limit)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }
}
