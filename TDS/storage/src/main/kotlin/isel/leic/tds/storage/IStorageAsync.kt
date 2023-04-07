package isel.leic.tds.storage

/**
 * Storage contract.
 * @param K Type of [T] id
 * @param T Type of the domain entity
 * @param U Type of what the "database" will store (strings, ...) so we can (de)serialize it later
 */
interface IStorageAsync <K, T, U> {

    /** Serializer object that will implement the from/to methods. */
    val serializer: ISerializer<T, U>

    /**
     * Creates a new entity identified by [id].
     * @throws Exception if already exists an entity with the given [id].
     */
    suspend fun new(id: K): T

    /** Returns the entity associated with [id], or null if not found. */
    suspend fun load(id: K): T?

    /** Saves the entity ([obj]) associated with [id]. */
    suspend fun save(id: K, obj: T)

    suspend fun delete(id: K)

}