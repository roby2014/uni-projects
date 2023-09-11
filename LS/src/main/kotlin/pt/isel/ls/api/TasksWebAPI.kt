package pt.isel.ls.api

import kotlinx.datetime.Clock
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.contract.ui.swagger.swaggerUiWebjar
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.format.Argo
import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import pt.isel.ls.api.routers.boards.BoardRouter
import pt.isel.ls.api.routers.cards.CardRouter
import pt.isel.ls.api.routers.lists.BoardListRouter
import pt.isel.ls.api.routers.users.UserRouter
import pt.isel.ls.services.BoardListService
import pt.isel.ls.services.BoardService
import pt.isel.ls.services.CardService
import pt.isel.ls.services.UserService

class TasksWebAPI(
    userService: UserService,
    boardService: BoardService,
    boardListService: BoardListService,
    cardService: CardService
) {

    // represents all API routes
    private val apiContract = contract {
        renderer = OpenApi3(ApiInfo("Tasks API", "v0.1.0"), Argo)
        descriptionPath = "/docs"

        routes += UserRouter(userService).routes
        routes += BoardRouter(boardService).routes
        routes += BoardListRouter(boardListService).routes
        routes += CardRouter(cardService).routes

        routes += "/ping" bindContract Method.GET to { _ -> Response(Status.OK).body("pong") }
        routes += "/date" bindContract Method.GET to { _ -> Response(Status.OK).body(Clock.System.now().toString()) }
    }

    // route handler
    val handler = routes(
        "/api" bind filter.then(apiContract),

        // Bind redoc to the root path (generates docs from openapi)
        /*
        redocWebjar {
            url = "/api/docs"
            pageTitle = "Tasks API Docs"
            options["disable-search"] = "true"
        },
        */

        // Bind Swagger UI to another path
        "/swagger" bind swaggerUiWebjar {
            url = "/api/docs"
            pageTitle = "Tasks API Docs"
            displayOperationId = true
        },

        singlePageApp(ResourceLoader.Directory("static-content"))
    )
}
