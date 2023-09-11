package pt.isel.ls.api

import org.http4k.core.Request
import org.http4k.core.HttpTransaction
import org.http4k.core.Filter
import org.http4k.filter.ResponseFilters
import org.http4k.lens.Path

const val DEFAULT_SKIP = 0
const val DEFAULT_LIMIT = 100

/**
 * Utility function to extract 'skip' and 'limit' query parameters.
 * In case they are invalid, the default values ([DEFAULT_LIMIT] & [DEFAULT_SKIP]) will be used.
 * @return Pair of: Skip to Limit.
 */
fun getPaginationParameters(request: Request): Pair<Int, Int> {
    val skip = request.query("skip")?.toIntOrNull() ?: DEFAULT_SKIP
    val limit = request.query("limit")?.toIntOrNull() ?: DEFAULT_LIMIT
    return skip to limit
}

val authorizationMiddleware: (String) -> Boolean = { token ->
    when {
        token.isNotEmpty() -> true // if (tokenIsValid)
        else -> false
    }
}

val idLens = Path.of("id")

val filter: Filter = ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
    println("[api filter] ${tx.labels} took ${tx.duration}")
}

/**
 * Sets request bearer token in authorization header.
 *
 * @param token bearer token
 * @return request with bearer token
 */
fun Request.token(token: String): Request =
    this.header("Authorization", "Bearer $token")

/**
 * Gets request bearer token in authorization header.
 *
 * @return request with bearer token extracted
 */
fun Request.getToken(): String = this.header("Authorization")!!.removePrefix("Bearer ")
// we can force because the [authMiddleware] will check if token exists
