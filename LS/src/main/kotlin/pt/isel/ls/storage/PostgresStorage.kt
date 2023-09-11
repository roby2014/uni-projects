package pt.isel.ls.storage

import io.github.cdimascio.dotenv.dotenv
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.models.Board
import pt.isel.ls.models.BoardList
import pt.isel.ls.models.Card
import pt.isel.ls.models.CardToBoardList
import pt.isel.ls.models.User
import java.sql.Connection
import java.sql.Date
import java.sql.Types
import java.util.UUID

object PostgresStorage : IStorage {
    private val dataSource = PGSimpleDataSource()

    init {
        val dotenv = dotenv() // get environment variables from ".env" in case we dont find it in path
        val host = System.getenv("DB_HOST") ?: dotenv["DB_HOST"]
        val name = System.getenv("DB_NAME") ?: dotenv["DB_NAME"]
        val user = System.getenv("DB_USER") ?: dotenv["DB_USER"]
        val pw = System.getenv("DB_PW") ?: dotenv["DB_PW"]
        dataSource.setURL("jdbc:postgresql://$host/$name?user=$user&password=$pw")

        // if dev mode, insert dummy data
        val api = System.getenv("API") ?: dotenv["API"]
        if (api == "DEV") {
            insertDummyDataForTesting()
        }
    }

    fun getConnection(): Connection = dataSource.connection

    override fun createUser(name: String, email: String, password: String, token: UUID): User? {
        return dataSource.connection.execute { conn ->
            val query =
                "INSERT INTO users (name, email, password, token) VALUES (?, ?, ?, ?) returning id, name, email, token"
            val statement = conn.prepareStatement(query)
            statement.setString(1, name)
            statement.setString(2, email)
            statement.setString(3, password)
            statement.setString(4, token.toString())

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val id = resultSet.getInt("id")
                val returnedName = resultSet.getString("name")
                val returnedEmail = resultSet.getString("email")
                val returnedToken = resultSet.getString("token")
                User(id, returnedName, returnedEmail, returnedToken)
            } else {
                null
            }
        }
    }

    override fun getUser(userId: Int): User? {
        return dataSource.connection.execute { conn ->
            val query = "SELECT * FROM users WHERE id = ?"
            val statement = conn.prepareStatement(query)
            statement.setInt(1, userId)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val id = resultSet.getInt("id")
                val name = resultSet.getString("name")
                val email = resultSet.getString("email")
                val password = resultSet.getString("password")
                val token = resultSet.getString("token")
                User(id, name, email, password, token)
            } else {
                null
            }
        }
    }

    override fun getUserToken(email: String, password: String): String? {
        return dataSource.connection.execute { conn ->
            val query = "SELECT token FROM users WHERE email = ? AND password = ?"
            val statement = conn.prepareStatement(query)
            statement.setString(1, email)
            statement.setString(2, password)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                resultSet.getString("token")
            } else {
                null
            }
        }
    }

    override fun getUserIdByToken(token: String): Int? {
        return dataSource.connection.execute { conn ->
            val query = "SELECT id FROM users WHERE token = ?"
            val statement = conn.prepareStatement(query)
            statement.setString(1, token)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                resultSet.getInt("id")
            } else {
                null
            }
        }
    }

    override fun getUserBoards(userId: Int, skip: Int, limit: Int): List<Board> {
        val boards = mutableListOf<Board>()
        dataSource.connection.execute { conn ->
            val query =
                "SELECT * FROM boards WHERE id IN (SELECT board_id FROM board_users WHERE user_id = ?) LIMIT ? OFFSET ?"
            val stm = conn.prepareStatement(query)
            stm.setInt(1, userId)
            stm.setInt(2, limit)
            stm.setInt(3, skip)

            val rs = stm.executeQuery()
            while (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val description = rs.getString("description")
                boards.add(Board(id, name, description))
            }
        }
        return boards
    }

    override fun addUserToBoard(userId: Int, boardId: Int) {
        dataSource.connection.execute { conn ->
            val query = "INSERT INTO board_users (board_id, user_id) VALUES (?, ?)"
            val stm = conn.prepareStatement(query)
            stm.setInt(1, boardId)
            stm.setInt(2, userId)
            stm.executeUpdate()
        }
    }

    override fun createBoard(name: String, description: String): Board {
        var generatedId = -1
        dataSource.connection.execute { conn ->
            val query = "INSERT INTO boards (name, description) VALUES (?, ?) RETURNING id"
            val stm = conn.prepareStatement(query)
            stm.setString(1, name)
            stm.setString(2, description)

            val rs = stm.executeQuery()
            if (rs.next()) {
                generatedId = rs.getInt(1)
            }
        }
        return Board(generatedId, name, description)
    }

    override fun getBoard(boardId: Int): Board? {
        var board: Board? = null
        dataSource.connection.execute { conn ->
            val query = "SELECT * FROM boards WHERE id = ?"
            val stm = conn.prepareStatement(query)
            stm.setInt(1, boardId)

            val rs = stm.executeQuery()
            if (rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val description = rs.getString("description")
                board = Board(id, name, description)
            }
        }
        return board
    }

    override fun searchBoards(data: String): List<Board> {
        val result = mutableListOf<Board>()

        val query = "SELECT id, name, description FROM boards WHERE id = ? OR name LIKE ? OR description LIKE ?"
        dataSource.connection.use { conn ->
            val statement = conn.prepareStatement(query)
            statement.setInt(1, data.toIntOrNull() ?: -1)
            statement.setString(2, "%$data%")
            statement.setString(3, "%$data%")
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val board = Board(
                    id = resultSet.getInt("id"),
                    name = resultSet.getString("name"),
                    description = resultSet.getString("description")
                )
                result.add(board)
            }
        }

        return result
    }

    override fun createBoardList(name: String, boardId: Int): BoardList {
        return dataSource.connection.execute { conn ->
            val query =
                "INSERT INTO lists (board_id, name, position) VALUES (?, ?, (SELECT COUNT(*) FROM lists WHERE board_id = ?)) returning id, position"
            val statement = conn.prepareStatement(query)
            statement.setInt(1, boardId)
            statement.setString(2, name)
            statement.setInt(3, boardId)
            val rs = statement.executeQuery()

            if (rs.next()) {
                val generatedId = rs.getInt("id")
                val position = rs.getInt("position")
                BoardList(generatedId, boardId, name, position)
            } else {
                throw Exception("Failed to create board list")
            }
        }
    }

    override fun getBoardLists(boardId: Int, skip: Int, limit: Int): List<BoardList> {
        val boardLists = mutableListOf<BoardList>()
        dataSource.connection.execute { conn ->
            val query = "SELECT * FROM lists WHERE board_id = ? ORDER BY position LIMIT ? OFFSET ?"
            val statement = conn.prepareStatement(query)
            statement.setInt(1, boardId)
            statement.setInt(2, limit)
            statement.setInt(3, skip)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val listId = resultSet.getInt("id")
                val listName = resultSet.getString("name")
                val position = resultSet.getInt("position")
                boardLists.add(BoardList(listId, boardId, listName, position))
            }
        }
        return boardLists
    }

    override fun getBoardUsersList(boardId: Int, skip: Int, limit: Int): List<User> {
        val boardUsers = mutableListOf<User>()
        dataSource.connection.execute { conn ->
            val query = """
            SELECT u.id, u.name, u.email, u.password, u.token
            FROM users u
            JOIN board_users bu ON u.id = bu.user_id
            WHERE bu.board_id = ? LIMIT ? OFFSET ?
            """.trimIndent()

            val statement = conn.prepareStatement(query)
            statement.setInt(1, boardId)
            statement.setInt(2, limit)
            statement.setInt(3, skip)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val userId = resultSet.getInt("id")
                val name = resultSet.getString("name")
                val email = resultSet.getString("email")
                val password = resultSet.getString("password")
                val token = resultSet.getString("token")
                boardUsers.add(User(userId, name, email, password, token))
            }
        }
        return boardUsers
    }

    override fun addListToBoard(boardId: Int, list: BoardList) {
        dataSource.connection.execute { conn ->
            val query = "INSERT INTO lists (board_id, name, position) VALUES (?, ?, ?)"
            val statement = conn.prepareStatement(query)
            statement.setInt(1, boardId)
            statement.setString(2, list.name)
            statement.setInt(3, list.position)
            statement.executeUpdate()
        }
    }

    override fun getCardsFromList(listId: Int, skip: Int, limit: Int): List<Card> {
        val cards = mutableListOf<Card>()
        dataSource.connection.execute { conn ->
            val query =
                "SELECT * FROM cards INNER JOIN list_cards ON cards.id = list_cards.card_id WHERE list_id = ? LIMIT ? OFFSET ?"
            val statement = conn.prepareStatement(query)
            statement.setInt(1, listId)
            statement.setInt(2, limit)
            statement.setInt(3, skip)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val cardId = resultSet.getInt("id")
                val cardName = resultSet.getString("name")
                val cardDescription = resultSet.getString("description")
                val creationDate =
                    resultSet.getTimestamp("creation_date").toLocalDateTime().toLocalDate().toKotlinLocalDate()
                val conclusionDate =
                    resultSet.getTimestamp("conclusion_date")?.toLocalDateTime()?.toLocalDate()?.toKotlinLocalDate()

                cards.add(Card(cardId, cardName, cardDescription, creationDate, conclusionDate))
            }
        }
        return cards
    }

    override fun getList(listId: Int): BoardList? {
        return dataSource.connection.execute { conn ->
            val statement = conn.prepareStatement(
                "SELECT * FROM lists WHERE id = ?"
            )
            statement.setInt(1, listId)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val listName = resultSet.getString("name")
                val boardId = resultSet.getInt("board_id")
                val position = resultSet.getInt("position")
                BoardList(listId, boardId, listName, position)
            } else {
                null
            }
        }
    }

    override fun deleteList(listId: Int) {
        dataSource.connection.execute { conn ->
            val listCardsQuery = "DELETE FROM list_cards WHERE list_id = ?"
            val listCardsDeleteStatement = conn.prepareStatement(listCardsQuery)
            listCardsDeleteStatement.setInt(1, listId)
            listCardsDeleteStatement.executeUpdate()

            val cardsQuery = "DELETE FROM lists WHERE id = ?"
            val cardsDeleteStatement = conn.prepareStatement(cardsQuery)
            cardsDeleteStatement.setInt(1, listId)
            cardsDeleteStatement.executeUpdate()
        }
    }

    override fun createCard(
        listId: Int,
        name: String,
        description: String,
        creationDate: LocalDate,
        conclusionDate: LocalDate?
    ): Card {
        return dataSource.connection.execute { conn ->
            val query =
                "INSERT INTO cards(name, description, creation_date, conclusion_date) VALUES (?, ?, ?, ?) returning id"
            val statement = conn.prepareStatement(query)
            statement.setString(1, name)
            statement.setString(2, description)
            statement.setDate(3, Date.valueOf(creationDate.toJavaLocalDate()))
            if (conclusionDate != null) {
                statement.setDate(4, Date.valueOf(conclusionDate.toJavaLocalDate()))
            } else {
                statement.setNull(4, Types.DATE)
            }
            val rs = statement.executeQuery()
            val cardId = when (rs.next()) {
                true -> rs.getInt("id")
                else -> throw Exception("Could not create card...")
            }

            val maxIndexQuery = "SELECT MAX(index) FROM list_cards WHERE list_id = ?"
            val maxIndexStatement = conn.prepareStatement(maxIndexQuery)
            maxIndexStatement.setInt(1, listId)
            val maxIndexResultSet = maxIndexStatement.executeQuery()
            val maxIndex = when (maxIndexResultSet.next()) {
                true -> maxIndexResultSet.getInt(1)
                else -> throw Exception("Could not get max index of card...")
            }

            val insertListCardQuery = "INSERT INTO list_cards (list_id, card_id, index) VALUES (?, ?, ?)"
            val insertListCardStatement = conn.prepareStatement(insertListCardQuery)
            insertListCardStatement.setInt(1, listId)
            insertListCardStatement.setInt(2, cardId)
            insertListCardStatement.setInt(3, maxIndex + 1)
            insertListCardStatement.executeUpdate()

            Card(cardId, name, description, creationDate, conclusionDate)
        }
    }

    override fun getCard(cardId: Int): Card? {
        return dataSource.connection.execute { conn ->
            val query = "SELECT * FROM cards WHERE id = ?"
            val statement = conn.prepareStatement(query)
            statement.setInt(1, cardId)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val name = resultSet.getString("name")
                val description = resultSet.getString("description")
                val creationDate = resultSet.getDate("creation_date").toLocalDate().toKotlinLocalDate()
                val conclusionDate = resultSet.getDate("conclusion_date")?.toLocalDate()?.toKotlinLocalDate()
                Card(cardId, name, description, creationDate, conclusionDate)
            } else {
                null
            }
        }
    }

    override fun moveCard(cardId: Int, listId: Int, newIndex: Int) {
        dataSource.connection.execute { conn ->
            val moveCardToIndexQuery = "CALL move_card_to_index(?, ?, ?)"
            val moveCardToIndexStatement = conn.prepareStatement(moveCardToIndexQuery)
            moveCardToIndexStatement.setInt(1, cardId)
            moveCardToIndexStatement.setInt(2, listId)
            moveCardToIndexStatement.setInt(3, newIndex)
            moveCardToIndexStatement.executeUpdate()
        }
    }

    override fun deleteCard(cardId: Int) {
        dataSource.connection.execute { conn ->
            val listCardsQuery = "DELETE FROM list_cards WHERE card_id = ?"
            val listCardsDeleteStatement = conn.prepareStatement(listCardsQuery)
            listCardsDeleteStatement.setInt(1, cardId)
            listCardsDeleteStatement.executeUpdate()

            val cardsQuery = "DELETE FROM cards WHERE id = ?"
            val cardsDeleteStatement = conn.prepareStatement(cardsQuery)
            cardsDeleteStatement.setInt(1, cardId)
            cardsDeleteStatement.executeUpdate()

            conn.commit()
        }
    }

    override fun getCardsToList(listId: Int): List<CardToBoardList> {
        return dataSource.connection.execute { conn ->
            val query = """
            SELECT lc.card_id, lc.list_id, lc.index 
            FROM list_cards lc 
            INNER JOIN cards c ON lc.card_id = c.id 
            WHERE lc.list_id = ?
            ORDER BY lc.index
        """
            val statement = conn.prepareStatement(query)
            statement.setInt(1, listId)
            val result = statement.executeQuery()
            val cards = mutableListOf<CardToBoardList>()
            while (result.next()) {
                val cardId = result.getInt("card_id")
                val index = result.getInt("index")
                cards.add(CardToBoardList(cardId, listId, index))
            }
            cards
        }
    }

    override fun getListIdFromCard(cardId: Int): Int? {
        return dataSource.connection.execute { conn ->
            val query = """
            SELECT lc.card_id, lc.list_id, lc.index 
            FROM list_cards lc 
            INNER JOIN cards c ON lc.card_id = c.id 
            WHERE lc.card_id = ?
        """
            val statement = conn.prepareStatement(query)
            statement.setInt(1, cardId)
            val result = statement.executeQuery()
            when (result.next()) {
                true -> result.getInt("list_id")
                false -> null
            }
        }
    }

    override fun insertDummyDataForTesting() {
        dataSource.connection.execute { conn ->
            conn.runScript("src/main/sql/reset.sql")
            conn.runScript("src/main/sql/create_schema.sql")
            conn.runScript("src/main/sql/insert_dummy_data.sql")
        }
    }
}
