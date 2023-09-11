package pt.isel.ls.api.routers.users

import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(val email: String, val password: String)
