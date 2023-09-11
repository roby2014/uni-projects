package pt.isel.ls.storage

import java.io.File
import java.sql.Connection

/**
 * Runs a function [fn] with auto commit set to false.
 * This way it allows atomicity. In case something goes wrong it rollbacks.
 *
 * @receiver Database connection.
 * @param fn function that interacts with the database.
 * @return Function [fn] result.
 */
fun <T> Connection.execute(fn: (Connection) -> T): T {
    return try {
        this.autoCommit = false
        val result = fn(this)
        this.commit()
        result
    } catch (e: Exception) {
        this.rollback()
        throw e
    } finally {
        this.close()
    }
}

/** Runs an SQL script from a given [scriptPath] file. */
fun Connection.runScript(scriptPath: String) {
    val script = File(scriptPath).readText()
    prepareStatement(script).execute()
}
