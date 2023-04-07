package isel.leic.tds.storage

/**
 * Storage contract.
 * @param K Type of [T] id
 * @param T Type of the domain entity
 * @param U Type of what the "database" will store (strings, ...) so we can (de)serialize it later
 */
interface IStorage<K, T, U> {

    /** Serializer object that will implement the from/to methods. */
    val serializer: ISerializer<T, U>

    /**
     * Creates a new entity identified by [id].
     * @throws Exception if already exists an entity with the given [id].
     */
    fun new(id: K): T

    /** Returns the entity associated with [id], or null if not found. */
    fun load(id: K): T?

    /** Saves the entity ([obj]) associated with [id]. */
    fun save(id: K, obj: T)

    /** Deletes the entity identified by [id]. */
    fun delete(id: K)

}