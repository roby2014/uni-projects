package pt.isel.ls.api.routers.boards

import kotlinx.serialization.Serializable

@Serializable
data class BoardDTO(
    val name: String,
    val description: String
)
