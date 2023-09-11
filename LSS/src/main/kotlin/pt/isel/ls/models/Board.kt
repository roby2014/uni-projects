package pt.isel.ls.models

import kotlinx.serialization.Serializable

@Serializable
data class Board(
    val id: Int,
    val name: String,
    val description: String
)
