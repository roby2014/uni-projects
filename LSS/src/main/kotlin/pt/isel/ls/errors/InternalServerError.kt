package pt.isel.ls.errors

/**
 * Thrown when an internal server error occurs.
 *
 * @param message exception message
 */
class InternalServerError(message: String = "") : Exception(message)
