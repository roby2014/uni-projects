package isel.leic.tds.storage


import org.litote.kmongo.coroutine.*


class MongoStorageAsync<T>(
    private val collectionName: String,
    private val factory: (String) -> T,
    override val serializer: ISerializer<T, String>,
    database: CoroutineDatabase
) : IStorageAsync<String,T,String>
{
    val collection = database.getCollection<Doc>(collectionName)

    /**
     * Throws Exception if already exists a document with the same Id.
     */
    override suspend fun new(id: String): T {
        /**
         * Validate that id is unique.
         */
        require(load(id) == null) { "There is already a document with given id $id" }
        /**
         * Create a new entity T and save in on file system.
         */
        val obj = factory(id)
        val objStr = serializer.serialize(obj)
        collection.insertOne(Doc(id, objStr))
        return obj
    }

    override suspend fun load(id: String): T? {
        val doc = collection.findOneById(id) ?: return null
        val objStr = doc.obj
        return serializer.deserialize(objStr)
    }

    override suspend fun save(id: String, obj: T) {
        require(load(id) != null) { "There is no document with given id $id" }
        val objStr = serializer.serialize(obj)
        collection.replaceOneById(id, Doc(id, objStr))
    }

    override suspend fun delete(id: String) {
        require(load(id) != null) { "There is no document with given id $id" }
        collection.deleteOneById(id)
    }

}
