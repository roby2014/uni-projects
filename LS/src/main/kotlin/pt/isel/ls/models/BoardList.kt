package pt.isel.ls.models

import kotlinx.serialization.Serializable

@Serializable
data class BoardList(
    val id: Int,
    val boardId: Int?,
    val name: String,
    val position: Int
)
