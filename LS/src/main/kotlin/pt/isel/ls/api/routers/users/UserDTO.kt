package pt.isel.ls.api.routers.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val name: String,
    val email: String,
    val password: String
)
