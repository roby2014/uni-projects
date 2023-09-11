package pt.isel.ls.models

import kotlinx.serialization.Serializable

@Serializable
data class CardToBoardList(
    val cardId: Int,
    val listId: Int,
    val index: Int
)
