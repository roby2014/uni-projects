package isel.leic.tds.storage

/**
 * Serializer contract.
 * @param T type of the domain entity
 * @param U type of the data we want to convert to/from (e.g String, ..)
 * NOTE: deserialize(serialize(input)) = input
 */
interface ISerializer<T, U> {
    fun serialize(obj: T) : U
    fun deserialize(input: U): T
}