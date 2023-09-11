package pt.isel.ls.errors

import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Response
import org.http4k.core.Status

fun errorHandler(fn: () -> Response): Response {
    val result = try {
        return fn()
    } catch (ex: NotFoundException) {
        Status.NOT_FOUND to (ex.message ?: "Resource not found")
    } catch (ex: InternalServerError) {
        Status.INTERNAL_SERVER_ERROR to (ex.message ?: "Internal server error")
    } catch (ex: BadRequestException) {
        Status.BAD_REQUEST to (ex.message ?: "Bad request")
    } catch (ex: MissingFieldException) {
        Status.BAD_REQUEST to (ex.message ?: "Missing field")
    } catch (ex: UnauthorizedException) {
        Status.UNAUTHORIZED to (ex.message ?: "Unauthorized (no access to this resource)")
    } catch (ex: Exception) {
        Status.INTERNAL_SERVER_ERROR to "SOMETHING WENT WRONG :( -> ${ex.message}"
    }

    val (status, msg) = result
    return Response(status)
        .header("content-type", "application/json")
        .body(Json.encodeToString(ErrorDTO(status.code, msg)))
}
