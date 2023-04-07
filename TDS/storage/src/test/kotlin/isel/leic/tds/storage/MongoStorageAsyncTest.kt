package isel.leic.tds.storage

import com.mongodb.ConnectionString
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class MongoStorageTest {

    class Dummy(val _id: String, val name: String, val address: String)

    val connectionString =
        ConnectionString(
            "mongodb://Grupo04:MgUoyafupMf0yhht" +
                    "@ac-twljvbj-shard-00-00.hmkwhou.mongodb.net:27017," +
                    "ac-twljvbj-shard-00-01.hmkwhou.mongodb.net:27017," +
                    "ac-twljvbj-shard-00-02.hmkwhou.mongodb.net:27017/?" +
                    "ssl=true&replicaSet=atlas-2je3qn-shard-0&authSource=admin&retryWrites=true&w=majority"
        )

    val client = KMongo.createClient(connectionString).coroutine
    val database = client.getDatabase("LEIC32D-Grupo04")

    @BeforeTest fun setup() {
        runBlocking { database.getCollection<Dummy>("test").deleteOneById("test") }
    }

    @Test fun `Check connection`(){
        runBlocking {
            val collection = database.getCollection<Dummy>("test")
            collection.insertOne(Dummy(
                (0..Int.MAX_VALUE).random().toString(),
                "Ze Manel",
                "Rua Rosa")
            )
        }
    }
    @Test fun `check if storage value equal original value`(){
        fun Dummyfactory(id: String) : Dummy{
            return Dummy(id,"Mario","Itália")
        }


                class dummySerializer() : isel.leic.tds.storage.ISerializer<Dummy, String> {
                      override fun serialize(obj: Dummy): String {
                        return "id=${obj._id},name=${obj.name},address=${obj.address}"
                    }

                     override fun deserialize(input: String): Dummy {
                        val dummyValues = input.split(",")
                        val id = dummyValues[0].drop(3)
                        val name = dummyValues[1].drop(5)
                        val address = dummyValues[2].drop(8)
                        return Dummy(id, name, address)
                    }
                }

        val mongo = MongoStorageAsync<Dummy>("test", { id -> Dummyfactory(id) },dummySerializer(),database)
        runBlocking{
            /*val collection = database.getCollection<MongoStorageAsync<Dummy>>("test")
            collection.insertOne(MongoStorageAsync(
                "testStorage", { id -> Dummyfactory(id) },dummySerializer(),database

            ))*/
            mongo.new("test")
            val dummyValue = Dummy("1","Mario", "Itália")
            mongo.save("test",dummyValue)
            val storageValue = mongo.load("test")
            assertEquals(dummyValue._id,storageValue?._id)
            assertEquals(dummyValue.name,storageValue?.name)
            assertEquals(dummyValue.address,storageValue?.address)
        }
    }

}