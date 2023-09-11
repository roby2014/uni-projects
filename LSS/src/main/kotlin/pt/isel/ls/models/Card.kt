package pt.isel.ls.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: Int,
    val name: String,
    val description: String,
    val creationDate: LocalDate,
    val conclusionDate: LocalDate?
)
