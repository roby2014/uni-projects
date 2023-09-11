package pt.isel.ls.models

import kotlinx.serialization.Serializable

@Serializable
data class BoardUser(
    val boardId: Int,
    val userId: Int
)
