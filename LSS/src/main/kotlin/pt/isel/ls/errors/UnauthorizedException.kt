package pt.isel.ls.errors

/**
 * Thrown when there is no authorization to access certain resource.
 *
 * @param message exception message
 */
class UnauthorizedException(message: String = "") : Exception(message)
