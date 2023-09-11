package pt.isel.ls.api.routers.cards

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
import pt.isel.ls.api.getToken
import pt.isel.ls.api.idLens
import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.UnauthorizedException
import pt.isel.ls.errors.errorHandler
import pt.isel.ls.services.CardService

/**
 * A class that handles HTTP requests related to cards.
 *
 * @param services an instance of `CardService` to interact with the business logic layer.
 */
class CardRouter(private val services: CardService) {

    /** The list of routes handled by this router. */
    val routes: List<ContractRoute> = listOf(

        // GET /cards/{id}
        "/cards" / idLens meta {
            summary = "Get the detailed information of a card"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "Card information",
                Status.BAD_REQUEST to "Invalid card id",
                Status.NOT_FOUND to "Card not found"
            )
        } bindContract Method.GET to { id -> { req -> getCard(req, id.toIntOrNull()) } },

        // POST /cards/{id}
        "/cards" / idLens meta {
            summary = "Moves a card to a new destination"
            description = "Query parameters: 'listId' (destination list identifier) and 'newIndex' (new index for the card in the destination list)"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "",
                Status.BAD_REQUEST to "Invalid 'listId' query parameter",
                Status.BAD_REQUEST to "Invalid 'newIndex' query parameter",
                Status.BAD_REQUEST to "Invalid card id",
                Status.NOT_FOUND to "Card not found"
            )
        } bindContract Method.POST to { id -> { req -> moveCard(req, id.toIntOrNull()) } },

        // DELETE /cards/{id}
        "/cards" / idLens meta {
            summary = "Deletes a card"
            security = BearerAuthSecurity(authorizationMiddleware)
            returning(
                Status.OK to "",
                Status.BAD_REQUEST to "Invalid card id",
                Status.NOT_FOUND to "Card not found"
            )
        } bindContract Method.DELETE to { id -> { req -> deleteCard(req, id.toIntOrNull()) } }
    )

    /**
     * Handles the retrieval of a card with the specified ID.
     *
     * @param req the HTTP request.
     * @param cardId the ID of the card to retrieve.
     * @return the HTTP response containing the card object.
     */
    private fun getCard(req: Request, cardId: Int?): Response = errorHandler {
        cardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsCard(req.getToken(), cardId)) {
            throw UnauthorizedException("No access")
        }

        val res = services.getCard(cardId)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(res))
    }

    /**
     * Handles the moving of a card to a new destination.
     *
     * @param req the HTTP request.
     * @param cardId the ID of the card to move.
     * @return the HTTP response.
     * @throws BadRequestException if 'listId' query parameter is invalid or not present.
     */
    private fun moveCard(req: Request, cardId: Int?): Response = errorHandler {
        cardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsCard(req.getToken(), cardId)) {
            throw UnauthorizedException("No access")
        }

        val listId = req.query("listId")?.toIntOrNull()
            ?: throw BadRequestException("Invalid query parameter 'listId'")

        val newIndex = req.query("newIndex")?.toIntOrNull()
            ?: throw BadRequestException("Invalid query parameter 'newIndex'")

        services.moveCard(cardId, listId, newIndex)
        Response(Status.OK)
    }

    /**
     * Handles the deletion of a card.
     *
     * @param req the HTTP request.
     * @param cardId the ID of the card to delete.
     * @return the HTTP response.
     */
    private fun deleteCard(req: Request, cardId: Int?): Response = errorHandler {
        cardId ?: throw BadRequestException("Invalid 'id' parameter")

        if (!services.userOwnsCard(req.getToken(), cardId)) {
            throw UnauthorizedException("No access")
        }

        services.deleteCard(cardId)
        Response(Status.OK)
    }
}
