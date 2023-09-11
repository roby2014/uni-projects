package pt.isel.ls.errors

import kotlinx.serialization.Serializable

/** Represents an error object that is returned to the user when something goes wrong. */
@Serializable
data class ErrorDTO(val errorCode: Int, val errorDescription: String? = "")
