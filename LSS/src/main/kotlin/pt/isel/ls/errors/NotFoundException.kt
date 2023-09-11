package pt.isel.ls.errors

/**
 * Thrown when a resource was not found.
 *
 * @param message exception message
 */
class NotFoundException(message: String = "") : Exception(message)
