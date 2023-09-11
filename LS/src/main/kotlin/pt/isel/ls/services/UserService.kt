package pt.isel.ls.services

import pt.isel.ls.errors.BadRequestException
import pt.isel.ls.errors.InternalServerError
import pt.isel.ls.errors.NotFoundException
import pt.isel.ls.models.Board
import pt.isel.ls.models.User
import pt.isel.ls.storage.IStorage
import java.util.*

class UserService(private val storage: IStorage) {

    /**
     * Creates a new user.
     *
     * @param name User's name
     * @param email User's email
     * @return Pair of token to created user's id
     * @throws InternalServerError if error creating user
     */
    fun createUser(name: String, email: String, password: String): Pair<String, Int> {
        if (password.length < 4) {
            throw Exception("Password length must be at least 4 characters.")
        }

        val token = UUID.randomUUID()
        val user = storage.createUser(name, email, password, token)
            ?: throw InternalServerError("Error creating user.")
        return token.toString() to user.id
    }

    /**
     * Get user details.
     *
     * @param userId User's identifier
     * @return User object
     * @throws NotFoundException if user not found
     */
    fun getUser(userId: Int): User {
        return storage.getUser(userId) ?: throw NotFoundException("User with id '$userId' not found.")
    }

    /**
     * Get user boards.
     *
     * @param userId User's identifier
     * @param skip start position of the subsequence to return
     * @param limit length of the subsequence to return
     * @return List of board objects
     * @throws NotFoundException if user not found
     */
    fun getUserBoards(userId: Int, skip: Int, limit: Int): List<Board> {
        storage.getUser(userId) ?: throw NotFoundException("User with id '$userId' not found.")
        return storage.getUserBoards(userId, skip, limit)
    }

    /**
     * Returns users token by its [email] and [password].
     */
    fun getUserToken(email: String, password: String): String {
        return storage.getUserToken(email, password) ?: throw BadRequestException("Invalid credentials")
    }

    /**
     * Returns user's identifier by its [token].
     */
    fun getUserIdByToken(token: String): Int {
        return storage.getUserIdByToken(token) ?: throw BadRequestException("Invalid token")
    }
}
