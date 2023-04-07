package isel.leic.tds.checkers.tui

import isel.leic.tds.checkers.model.Board
import com.mongodb.ConnectionString
import isel.leic.tds.checkers.model.BoardStarting
import org.litote.kmongo.KMongo
import isel.leic.tds.storage.ISerializer
import isel.leic.tds.checkers.tui.commands.*
import isel.leic.tds.storage.MongoStorage
import isel.leic.tds.storage.FileStorage

fun main() {
    val boardSerializer = object : ISerializer<Board, String> {
        override fun serialize(obj: Board) = obj.serialize()
        override fun deserialize(input: String) = Board.deserialize(input)
    }
    val connectionString =
        ConnectionString(
            "mongodb://Grupo04:MgUoyafupMf0yhht" +
                    "@ac-twljvbj-shard-00-00.hmkwhou.mongodb.net:27017," +
                    "ac-twljvbj-shard-00-01.hmkwhou.mongodb.net:27017," +
                    "ac-twljvbj-shard-00-02.hmkwhou.mongodb.net:27017/?" +
                    "ssl=true&replicaSet=atlas-2je3qn-shard-0&authSource=admin&retryWrites=true&w=majority"
        )
    val client = KMongo.createClient(connectionString)
    val database = client.getDatabase("LEIC32D-Grupo04")
    val collectionName = "Checkers"
    //val storage = MongoStorage(collectionName, { BoardStarting() }, boardSerializer, database)
    val storage = FileStorage<String, Board>("./", { BoardStarting() }, boardSerializer)
    readCommands(availableGameCommands(storage))
}

/**
 * "Main game loop".
 * Waits for user input (commands), and executes it.
 * @param [commands] Possible game commands (following [ICommand] contract)
 */
fun readCommands(commands: Map<String, ICommand<Game>>) {
    var game: Game? = null
    while (true) {
        print("> ")
        val args = readln().trim().split(" ") // e.g. args = ["play", "from", "to"]
        val cmdName = args[0].uppercase()

        if (cmdName == "HELP") {
            println(commands.values.joinToString("\n") { it.syntax })
            continue
        }

        val cmd = commands[cmdName]
        if (cmd == null) {
            println("Unknown command '$cmdName'")
            continue
        }

        try {
            game = cmd.action(game, args.drop(1)) ?: break
            cmd.show(game)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}