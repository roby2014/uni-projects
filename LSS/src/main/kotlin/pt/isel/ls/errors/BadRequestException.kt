package pt.isel.ls.errors

/**
 * Thrown when a bad request occurs.
 *
 * @param message exception message
 */
class BadRequestException(message: String = "") : Exception(message)
