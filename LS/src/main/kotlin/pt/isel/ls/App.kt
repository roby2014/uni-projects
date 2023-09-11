package pt.isel.ls

import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.api.TasksWebAPI
import pt.isel.ls.services.BoardListService
import pt.isel.ls.services.BoardService
import pt.isel.ls.services.CardService
import pt.isel.ls.services.UserService
import pt.isel.ls.storage.IStorage

fun tasksServer(storage: IStorage, port: Int): Http4kServer {
    val userServices = UserService(storage)
    val boardServices = BoardService(storage)
    val boardListsServices = BoardListService(storage)
    val cardServices = CardService(storage)
    val webApi = TasksWebAPI(userServices, boardServices, boardListsServices, cardServices)
    return webApi.handler.asServer(Jetty(port))
}
