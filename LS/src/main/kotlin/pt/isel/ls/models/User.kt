package pt.isel.ls.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val token: String = ""
)
