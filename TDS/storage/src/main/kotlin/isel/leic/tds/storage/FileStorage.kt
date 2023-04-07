package isel.leic.tds.storage

import java.io.File

/**
 * [IStorage] implementation via file + text strings.
 * @param K Type of [T] id
 * @param T Type of the domain entity
 * @param folder Folder where the file will be stored
 * @param factory Function that returns a class object of type T
 */
class FileStorage<K, T>(
    private val folder: String,
    private val factory: (K) -> T,
    override val serializer: ISerializer<T, String>
) : IStorage<K, T, String> {

    /** Storage file path for entity identified by [id]. */
    private fun path(id: K) = "$folder/$id.txt"

    override fun new(id: K): T {
        val file = File(path(id))
        require(!file.exists()) { "There is already an entity with given id '$id'" }
        val obj = factory(id)
        val objStr = serializer.serialize(obj)
        file.writeText(objStr)
        return obj
    }

    override fun load(id: K): T? {
        val file = File(path(id))
        return when (file.exists()) {
            true -> serializer.deserialize(file.readText())
            else -> null
        }
    }

    override fun save(id: K, obj: T) {
        val file = File(path(id))
        require(file.exists()) { "There is no entity with given id '$id'" }
        val objStr = serializer.serialize(obj)
        file.writeText(objStr)
    }

    override fun delete(id: K) {
        val file = File(path(id))
        require(file.exists()) { "There is no entity with given id '$id'" }
        file.delete()
    }
}