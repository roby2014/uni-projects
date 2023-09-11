package pt.isel.ls

import io.github.cdimascio.dotenv.dotenv
import pt.isel.ls.storage.DataMemoryStorage
import pt.isel.ls.storage.PostgresStorage

fun main() {
    val dotenv = dotenv()
    val storage = when (System.getenv("SERVER_STORAGE") ?: dotenv["SERVER_STORAGE"]) {
        "LOCAL" -> DataMemoryStorage
        "REMOTE" -> PostgresStorage
        else -> throw Exception("SERVER_STORAGE has to be \"LOCAL\" or \"REMOTE\"")
    }
    val port = (System.getenv("PORT") ?: dotenv["PORT"]).toIntOrNull() ?: throw Exception("PORT env variable missing")
    val server = tasksServer(storage, port)

    server.start()
    println("server started listening on: localhost:$port")
    readln()
    server.stop()
    println("shutting down server")
}
