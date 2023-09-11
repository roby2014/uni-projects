package pt.isel.ls
/*

import io.github.cdimascio.dotenv.dotenv
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DBTests {
    private val dotenv = dotenv() // get environment variables from ".env"
    private val dataSource = PGSimpleDataSource()
    private val jdbcDatabaseURL = dotenv["JDBC_DATABASE_URL"]

    @BeforeTest
    fun setup() {
        dataSource.setURL(jdbcDatabaseURL)
    }

    @AfterTest
    fun end() {
        // dataSource.connection.close() // .use already closes it
    }

    @Test
    fun select() {
        var counter = 0
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from users")
            val rs = stm.executeQuery()
            while (rs.next()) {
                counter++
            }
            assert(counter != 0) // database should have some data
        }
    }

    @Test
    fun insert_and_select() {
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into users values (DEFAULT, 'dbtest', 'dbtest@gmail.com', 'abc123')")
            stm.executeUpdate()

            val stm2 = it.prepareStatement("select * from users where name = 'dbtest'")
            val rs2 = stm2.executeQuery()
            var counter = 0
            while (rs2.next()) {
                counter++
            }
            assert(counter != 0)
        }
    }

    @Test
    fun delete_and_select() {
        dataSource.connection.use {
            val stm = it.prepareStatement("delete from users where name = 'dbtest'")
            stm.executeUpdate()

            val stm2 = it.prepareStatement("select * from users where name = 'dbtest'")
            val rs2 = stm2.executeQuery()
            var counter = 0
            while (rs2.next()) {
                counter++
            }
            assert(counter == 0)
        }
    }
}
*/
