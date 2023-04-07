import isel.leic.tds.storage.FileStorage
import isel.leic.tds.storage.ISerializer
import java.io.File
import kotlin.test.*

class FileStorageTest {

    // Dummy data for testing purposes

    private data class DummyEntity(val id: Int, val name: String = "")

    private object DummySerializer : ISerializer<DummyEntity, String> {
        override fun serialize(obj: DummyEntity) = "${obj.id}:${obj.name}"

        override fun deserialize(input: String) =
            input.split(":").let {
                DummyEntity(it[0].toInt(), it[1])
            }
    }

    private val dummyTestFolder = "build"

    private val dummyId = 1337

    @BeforeTest
    fun setup() {
        val f = File("$dummyTestFolder/$dummyId.txt")
        if (f.exists()) f.delete()
    }

    // Tests

    @Test
    fun `Create an entity with existing id throws exception`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        fs.new(dummyId)
        assertEquals(
            "There is already an entity with given id '$dummyId'",
            assertFailsWith<IllegalArgumentException> { fs.new(dummyId) }.message
        )

    }

    @Test
    fun `Load an entity with unknown id returns null`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        assertNull(fs.load(111111))
    }

    @Test
    fun `Load an existent entity`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        fs.new(dummyId)
        assertNotNull(fs.load(dummyId))
    }

    @Test
    fun `Delete a non existent entity`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        val randomId = 1234
        assertEquals(
            "There is no entity with given id '$randomId'",
            assertFailsWith<IllegalArgumentException> { fs.delete(randomId) }.message
        )
    }

    @Test
    fun `Delete an existent entity`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        fs.new(dummyId)
        fs.delete(dummyId)
        assertNull(fs.load(dummyId))
    }

    @Test
    fun `Save a non existent entity`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        val randomId = 1234
        assertEquals(
            "There is no entity with given id '$randomId'",
            assertFailsWith<IllegalArgumentException> { fs.save(randomId, DummyEntity(randomId, "idk")) }.message
        )
    }

    @Test
    fun `Create, save and load an entity`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        fs.new(dummyId)
        fs.save(dummyId, DummyEntity(dummyId, "Bill"))
        val obj = fs.load(dummyId)
        assertNotNull(obj)
        assertEquals(dummyId, obj.id)
        assertEquals("Bill", obj.name)
    }

    @Test
    fun `Serializer test`() {
        val fs = FileStorage(dummyTestFolder, ::DummyEntity, DummySerializer)
        fs.new(dummyId)
        fs.save(dummyId, DummyEntity(dummyId, "Bill"))
        val obj = fs.load(dummyId)!!
        // deserialize(serialize(input)) = input
        assertEquals(fs.serializer.deserialize(fs.serializer.serialize(obj)), obj)
    }
}