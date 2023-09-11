package pt.isel.ls.api.routers.cards

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CardDTO(
    val name: String,
    val description: String,
    val dueDate: LocalDate? = null
)
